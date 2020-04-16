package com.droid47.petfriend.search.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.extensions.applyIOSchedulers
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.bookmark.domain.interactors.AddOrRemoveBookmarkUseCase
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
import com.droid47.petfriend.search.presentation.widgets.PetAdapter
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    application: Application,
    private val searchPetUseCase: SearchPetUseCase,
    private val updateFilterUseCase: UpdateFilterUseCase,
    private val addOrRemoveBookmarkUseCase: AddOrRemoveBookmarkUseCase,
    private val fetchAppliedFilterUseCase: FetchAppliedFilterUseCase
) : BaseAndroidViewModel(application), PetAdapter.OnItemClickListener {

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

    val eventLiveData = LiveEvent<Int>()

    init {
        attachSearchEvent()
        onPetStarred()
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
        fetchAppliedFilterUseCase.buildUseCaseObservable()
            .applyIOSchedulers()
            .subscribeWith(object : DisposableSubscriber<BaseStateModel<Filters>>() {
                override fun onComplete() {
                }

                override fun onNext(baseStateModel: BaseStateModel<Filters>) {
                    _filterItemLiveData.postValue(baseStateModel)
                }

                override fun onError(e: Throwable) {
                    _filterItemLiveData.postValue(Failure(e))
                    CrashlyticsExt.handleException(e)
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
                    registerRequest(REQUEST_UPDATE_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }


    fun updatePage() {
        updateFilter((obtainPagination().currentPage.plus(1)).toString(), PAGE_NUM)
    }

    fun resetToFirstPage() {
        updateFilter(PAGE_ONE.toString(), PAGE_NUM)
    }

    fun updateLocation(location: String) {
        updateFilter(location, LOCATION)
    }

    fun onFilterModified(filters: Filters) = searchSubject.onNext(filters)

    @SuppressLint("CheckResult")
    private fun attachSearchEvent() {
        searchSubject.debounce(60, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerRequest(REQUEST_SEARCH, it) }
            .switchMapSingle { filters ->
                getSearchList(filters)
            }.subscribe({
                _searchStateLiveData.postValue(it)
            }, {
                _searchStateLiveData.postValue(processError(it))
            })
    }

    @SuppressLint("CheckResult")
    private fun onPetStarred() {
        bookMarkSubject.debounce(300, TimeUnit.MILLISECONDS)
            .doOnSubscribe { registerRequest(REQUEST_BOOK_MARK_PET, it) }
            .switchMapSingle { bookMarkStatusAndPetPair ->
                addOrRemoveBookmarkUseCase.buildUseCaseSingle(bookMarkStatusAndPetPair.apply {
                    bookmarkedAt = System.currentTimeMillis()
                })
            }.applyIOSchedulers()
            .subscribe(this::onBookmarkPetSuccess, this::onPetStarError)
    }

    private fun onBookmarkPetSuccess(baseStateModel: BaseStateModel<PetEntity>) {
        when (baseStateModel) {
            is Success -> {
                val bookMarkResultList = obtainCurrentData().map { pet ->
                    pet.apply {
                        bookmarkStatus = if (pet == baseStateModel.data) {
                            baseStateModel.data.bookmarkStatus
                        } else {
                            pet.bookmarkStatus
                        }
                    }
                }
                if (bookMarkResultList.isNotEmpty()) {
                    _searchStateLiveData.postValue(
                        DefaultState(
                            obtainPagination(),
                            obtainCurrentLoadedAllItems(),
                            bookMarkResultList
                        )
                    )
                }
            }

            is Failure -> {
                onPetStarError(baseStateModel.error)
            }
        }
    }

    private fun onPetStarError(throwable: Throwable) {
        val err = throwable
    }

    @SuppressLint("CheckResult")
    private fun getSearchList(filters: Filters) =
        searchPetUseCase.buildUseCaseSingle(filters)
            .applyIOSchedulers()
            .map { processResponse(it) }
            .doOnSubscribe {
                _searchStateLiveData.postValue(updateLoadingState(filters.page.toInt()))
            }
            .onErrorReturn { processError(it) }

    private fun updateLoadingState(page: Int) =
        if (page == PAGE_ONE)
            LoadingState(obtainPagination(), false, emptyList())
        else
            PaginatingState(
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainCurrentData()
            )

    private fun processResponse(searchResponseEntity: SearchResponseEntity): SearchState {
        val animalList = searchResponseEntity.animals ?: emptyList()
        val pagination = searchResponseEntity.paginationEntity ?: PaginationEntity()
        val animalItemList =
            obtainCurrentData().toMutableList().apply {
                addAll(animalList)
            }.distinctBy { petEntity ->
                petEntity.id
            }
        val allItemsLoaded = pagination.currentPage == pagination.totalPages
        return if (animalItemList.isEmpty()) {
            EmptyState(pagination, allItemsLoaded, animalItemList)
        } else {
            DefaultState(pagination, allItemsLoaded, animalItemList)
        }
    }

    private fun processError(throwable: Throwable): SearchState {
        val isEmptyResult = obtainCurrentData().isEmpty()
        return if (isEmptyResult) {
            ErrorState(
                throwable,
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainCurrentData()
            )
        } else {
            PaginationErrorState(
                throwable,
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainCurrentData()
            )
        }
    }

    private fun obtainPagination() =
        searchStateLiveData.value?.paginationEntity ?: PaginationEntity()

    private fun obtainCurrentData() = searchStateLiveData.value?.data ?: emptyList()

    private fun obtainCurrentLoadedAllItems() = searchStateLiveData.value?.loadedAllItems ?: false

companion object {
    private const val REQUEST_SEARCH = 1001
    private const val REQUEST_UPDATE_FILTER = 1003
    private const val REQUEST_BOOK_MARK_PET = 1004
    private const val PAGE_ONE = 1
}
}