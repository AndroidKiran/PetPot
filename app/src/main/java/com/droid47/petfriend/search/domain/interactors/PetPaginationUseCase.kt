package com.droid47.petfriend.search.domain.interactors

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.droid47.petfriend.base.extensions.clearDisposable
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.search.data.models.search.PaginationEntity
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.presentation.models.*
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_ONE
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PetPaginationUseCase @Inject constructor(
    private val petDataSourceUseCase: PetDataSourceUseCase,
    private val fetchPetFromNetworkAndCacheDbUseCase: FetchPetFromNetworkAndCacheDbUseCase,
    private val fetchAppliedFilterUseCase: FetchAppliedFilterUseCase
) : PagedList.BoundaryCallback<PetEntity>() {

    private val searchSubject = PublishSubject.create<Boolean>().toSerialized()
    private val _searchStateLiveData = MutableLiveData<SearchState>()
    private val compositeDisposable = CompositeDisposable()
    val searchStateLiveData: LiveData<SearchState>
        get() = _searchStateLiveData

    init {
        subscribeToSearchSubject()
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        searchSubject.onNext(true)
    }

    override fun onItemAtEndLoaded(itemAtEnd: PetEntity) {
        super.onItemAtEndLoaded(itemAtEnd)
        if (searchStateLiveData.value?.loadedAllItems == true) return
        searchSubject.onNext(false)
    }

    fun buildPageListObservable(): Flowable<BaseStateModel<PagedList<PetEntity>>> =
        petDataSourceUseCase.buildUseCaseObservable(this)


    fun retry() {
        when (obtainPagination().currentPage) {
            PAGE_ONE -> onZeroItemsLoaded()
            else -> onItemAtEndLoaded(PetEntity())
        }
    }

    fun cancelPagination() {
        compositeDisposable.clearDisposable()
    }

    /*
    * Create search task
    * */

    @SuppressLint("CheckResult")
    private fun subscribeToSearchSubject() {
        searchSubject.debounce(60, TimeUnit.MILLISECONDS)
            .performSearchTask()
            .subscribe(object : DisposableObserver<SearchState>() {
                override fun onComplete() {
                }

                override fun onNext(searchState: SearchState) {
                    _searchStateLiveData.postValue(searchState)
                }

                override fun onError(e: Throwable) {
                    _searchStateLiveData.postValue(processError(e))
                }

            })
    }

    private fun Observable<Boolean>.performSearchTask(): Observable<SearchState> =
        switchMapSingle { isFirstPage ->
            fetchAppliedFilterUseCase.buildUseCaseSingle(isFirstPage)
                .doOnSubscribe {
                    _searchStateLiveData.postValue(updateLoadingState(isFirstPage))
                    compositeDisposable.add(it)
                }.flatMap { filters ->
                    fetchPetFromNetworkAndCacheDbUseCase.buildUseCaseSingle(filters)
                }.processResponse()
                .onErrorReturn {
                    processError(it)
                }
        }

    private fun Single<SearchResponseEntity>.processResponse(): Single<SearchState> =
        map { searchResponseEntity ->
            val pagination = searchResponseEntity.paginationEntity ?: PaginationEntity()
            val allItemsLoaded = pagination.currentPage >= pagination.totalPages
            val totalCount = obtainTotalCount().plus(searchResponseEntity.animals?.size ?: 0)
            return@map if (searchResponseEntity.animals.isNullOrEmpty() && pagination.currentPage == PAGE_ONE) {
                EmptyState(pagination, allItemsLoaded, totalCount)
            } else {
                DefaultState(pagination, allItemsLoaded, totalCount)
            }
        }

    private fun processError(throwable: Throwable): SearchState =
        if (PAGE_ONE == obtainPagination().currentPage) {
            ErrorState(
                throwable,
                obtainPagination(),
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )
        } else {
            PaginationErrorState(
                throwable,
                obtainPagination().apply {
                    currentPage = currentPage.minus(1)
                },
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )
        }

    private fun updateLoadingState(isFirstPage: Boolean): SearchState =
        if (isFirstPage)
            LoadingState(PaginationEntity(currentPage = PAGE_ONE), false, 0)
        else
            PaginatingState(
                obtainPagination().apply {
                    currentPage = this.currentPage.plus(1)
                },
                obtainCurrentLoadedAllItems(),
                obtainTotalCount()
            )


    private fun obtainPagination(): PaginationEntity =
        searchStateLiveData.value?.paginationEntity ?: PaginationEntity().apply {
            currentPage = 1
        }

    private fun obtainCurrentLoadedAllItems(): Boolean =
        searchStateLiveData.value?.loadedAllItems ?: false

    private fun obtainTotalCount(): Int = searchStateLiveData.value?.totalCount ?: 0

}

