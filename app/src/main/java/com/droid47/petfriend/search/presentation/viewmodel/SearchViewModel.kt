package com.droid47.petfriend.search.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.droid47.petfriend.base.extensions.applyIOSchedulers
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.bookmark.domain.interactors.DataSourceType
import com.droid47.petfriend.bookmark.domain.interactors.RemoveAllPetsUseCase
import com.droid47.petfriend.bookmark.domain.interactors.SubscribeToPetsUseCase
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.LOCATION
import com.droid47.petfriend.search.data.models.PAGE_NUM
import com.droid47.petfriend.search.data.models.search.PaginationEntity
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.domain.interactors.FetchAppliedFilterUseCase
import com.droid47.petfriend.search.domain.interactors.SearchPetUseCase
import com.droid47.petfriend.search.domain.interactors.UpdateFilterUseCase
import com.droid47.petfriend.search.presentation.models.*
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_ONE
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_SIZE
import com.droid47.petfriend.search.presentation.widgets.PagedListPetAdapter
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    application: Application,
    private val searchPetUseCase: SearchPetUseCase,
    private val updateFilterUseCase: UpdateFilterUseCase,
    private val fetchAppliedFilterUseCase: FetchAppliedFilterUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase,
    private val subscribeToPetsUseCase: SubscribeToPetsUseCase
) : BaseAndroidViewModel(application), PagedListPetAdapter.OnItemClickListener {

    private val _navigateToAnimalDetailsAction = LiveEvent<Pair<PetEntity, View>>()
    val navigateToAnimalDetailsAction: LiveEvent<Pair<PetEntity, View>>
        get() = _navigateToAnimalDetailsAction

    private val searchSubject = PublishSubject.create<Filters>().toSerialized()
    private val bookMarkSubject = PublishSubject.create<PetEntity>().toSerialized()

    private val _searchStateLiveData = MutableLiveData<SearchState>()
    val searchStateLiveData: LiveData<SearchState>
        get() = _searchStateLiveData

    private val _filterItemLiveData = LiveEvent<BaseStateModel<Filters>>()
    val filterItemLiveData: LiveEvent<BaseStateModel<Filters>>
        get() = _filterItemLiveData

    val eventLiveData = LiveEvent<Long>()

    private val _petsLiveData: MutableLiveData<BaseStateModel<out PagedList<PetEntity>>> =
        MutableLiveData(Loading())
    val petsLiveData: LiveData<BaseStateModel<out PagedList<PetEntity>>>
        get() = _petsLiveData

    init {
        attachSearchEvent()
        listenToFilterUpdate()
    }

    override fun onBookMarkClick(petEntity: PetEntity) {
        bookMarkSubject.onNext(petEntity)
    }

    override fun onItemClick(petEntity: PetEntity, view: View) {
        _navigateToAnimalDetailsAction.postValue(Pair(petEntity, view))
    }

    @SuppressLint("CheckResult")
    private fun listenToFilterUpdate() {
        fetchAppliedFilterUseCase.buildUseCaseObservable(Unit)
            .applyIOSchedulers()
            .subscribeWith(object : DisposableSubscriber<BaseStateModel<Filters>>() {
                override fun onComplete() {
                }

                override fun onNext(baseStateModel: BaseStateModel<Filters>) {
                    _filterItemLiveData.postValue(baseStateModel)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                    _filterItemLiveData.postValue(Failure(e))
                }
            })
    }

    private fun updateFilter(name: String, type: String) {
        updateFilterUseCase.execute(
            FilterItemEntity(name, type, true),
            object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_UPDATE_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    fun updatePage() {
        updateFilter((obtainPagination().currentPage.plus(1)).toString(), PAGE_NUM)
    }

    fun updateLocation(location: String) {
        updateFilter(location, LOCATION)
    }

    fun onFilterModified(filters: Filters) {
        invalidateDataSource(filters)
        searchSubject.onNext(filters)
    }

    @SuppressLint("CheckResult")
    private fun attachSearchEvent() {
        searchSubject.debounce(60, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerDisposableRequest(REQUEST_SEARCH, it) }
            .switchMapSingle { filters ->
                getSearchList(filters)
            }.subscribe({
                _searchStateLiveData.postValue(it)
            }, {
                _searchStateLiveData.postValue(processError(it))
            })
    }

    @SuppressLint("CheckResult")
    private fun getSearchList(filters: Filters) =
        when (filters.page.toInt()) {
            PAGE_ONE -> onFirstPageLoad(filters)
            else -> onNextPageLoad(filters)
        }.doOnSubscribe {
            _searchStateLiveData.postValue(updateLoadingState(filters.page.toInt()))
        }.applyIOSchedulers()
            .flatMap { stateModel ->
                when (stateModel) {
                    is Success -> {
                        searchPetUseCase.addPetsToDb(
                            stateModel.data.animals ?: emptyList()
                        ).map { stateModel.data }
                    }
                    is Failure -> Single.error(stateModel.error)
                    else -> Single.error(IllegalStateException("Search eMpty state"))
                }
            }
            .map { processResponse(it) }
            .onErrorReturn { processError(it) }

    private fun onFirstPageLoad(filters: Filters): Single<BaseStateModel<SearchResponseEntity>> =
        Single.zip(
            searchPetUseCase.buildUseCaseSingle(filters),
            removeAllPetsUseCase.buildUseCaseSingle(false),
            BiFunction { stateModel: BaseStateModel<SearchResponseEntity>, id: Int ->
                stateModel
            }
        )

    private fun onNextPageLoad(filters: Filters): Single<BaseStateModel<SearchResponseEntity>> =
        searchPetUseCase.buildUseCaseSingle(filters)

    private fun updateLoadingState(page: Int) =
        if (page == PAGE_ONE)
            LoadingState(PaginationEntity(currentPage = PAGE_ONE), false, 0)
        else
            PaginatingState(
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )

    private fun processResponse(searchResponseEntity: SearchResponseEntity): SearchState {
        val pagination = searchResponseEntity.paginationEntity ?: PaginationEntity()
        val allItemsLoaded = pagination.currentPage == pagination.totalPages
        val totalCount = obtainTotalCount().plus(searchResponseEntity.animals?.size ?: 0)
        return if (searchResponseEntity.animals.isNullOrEmpty() && totalCount <= PAGE_SIZE) {
            EmptyState(pagination, allItemsLoaded, totalCount)
        } else {
            DefaultState(pagination, allItemsLoaded, totalCount)
        }
    }

    private fun processError(throwable: Throwable): SearchState {
        val isFirstPage = obtainTotalCount() < PAGE_SIZE
        return if (isFirstPage) {
            ErrorState(
                throwable,
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )
        } else {
            PaginationErrorState(
                throwable,
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )
        }
    }

    private fun obtainPagination(): PaginationEntity =
        searchStateLiveData.value?.paginationEntity ?: PaginationEntity()

    private fun obtainCurrentLoadedAllItems(): Boolean =
        searchStateLiveData.value?.loadedAllItems ?: false

    private fun obtainTotalCount(): Int = searchStateLiveData.value?.totalCount ?: 0

    private fun invalidateDataSource(filters: Filters) {
        val isFirstPage = filters.page.toIntOrNull() == PAGE_ONE
        val dataSourceType =
            if (filters.location.isNullOrEmpty()) DataSourceType.RecentType else DataSourceType.DistanceType
        if (isFirstPage) {
            subscribeToPetsUseCase.buildUseCaseObservable(Pair(dataSourceType, filters.type ?: ""))
                .doOnSubscribe {
                    registerSubscriptionRequest(REQUEST_DATA_SOURCE, it)
                }
                .subscribe(object :
                    DisposableSubscriber<BaseStateModel<out PagedList<PetEntity>>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(baseStateModel: BaseStateModel<out PagedList<PetEntity>>?) {
                        _petsLiveData.postValue(baseStateModel)
                    }

                    override fun onError(t: Throwable?) {
                        CrashlyticsExt.logHandledException(t ?: return)
                    }

                })
        }
    }

    companion object {
        private const val REQUEST_SEARCH = 1001L
        private const val REQUEST_UPDATE_FILTER = 1003L
        private const val REQUEST_DATA_SOURCE = 1005L
    }
}