package com.droid47.petpot.search.domain.interactors

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.droid47.petpot.base.extensions.clearDisposable
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.search.data.models.search.PaginationEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.data.models.search.SearchResponseEntity
import com.droid47.petpot.search.presentation.models.*
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_ONE
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
) : PagedList.BoundaryCallback<SearchPetEntity>() {

    private val searchSubject = PublishSubject.create<Boolean>().toSerialized()
    private val compositeDisposable = CompositeDisposable()

    private val _itemPaginationStateLiveData = MutableLiveData<ItemPaginationState>()
    val itemPaginationStateLiveData: LiveData<ItemPaginationState>
        get() = _itemPaginationStateLiveData

    init {
        subscribeToSearchSubject()
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        searchSubject.onNext(true)
    }

    override fun onItemAtEndLoaded(itemAtEnd: SearchPetEntity) {
        super.onItemAtEndLoaded(itemAtEnd)
        if (itemPaginationStateLiveData.value?.loadedAllItems == true) return
        searchSubject.onNext(false)
    }

    fun buildPageListObservable(): Flowable<BaseStateModel<PagedList<SearchPetEntity>>> =
        petDataSourceUseCase.buildUseCaseObservable(this)


    fun retry() {
        when (obtainPagination().currentPage) {
            PAGE_ONE -> onZeroItemsLoaded()
            else -> onItemAtEndLoaded(SearchPetEntity())
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
        searchSubject.debounce(400, TimeUnit.MILLISECONDS)
            .performSearchTask()
            .subscribe(object : DisposableObserver<ItemPaginationState>() {
                override fun onComplete() {
                }

                override fun onNext(itemPaginationState: ItemPaginationState) {
                    _itemPaginationStateLiveData.postValue(itemPaginationState)
                }

                override fun onError(e: Throwable) {
                    _itemPaginationStateLiveData.postValue(processError(e))
                }

            })
    }

    private fun Observable<Boolean>.performSearchTask(): Observable<ItemPaginationState> =
        switchMapSingle { isFirstPage ->
            fetchAppliedFilterUseCase.buildUseCaseSingle(isFirstPage)
                .doOnSubscribe {
                    _itemPaginationStateLiveData.postValue(updateLoadingState(isFirstPage))
                    compositeDisposable.add(it)
                }.flatMap { filters ->
                    fetchPetFromNetworkAndCacheDbUseCase.buildUseCaseSingle(filters)
                }.processResponse()
                .onErrorReturn {
                    processError(it)
                }
        }

    private fun Single<SearchResponseEntity>.processResponse(): Single<ItemPaginationState> =
        map { searchResponseEntity ->
            val pagination = searchResponseEntity.paginationEntity ?: PaginationEntity()
            val allItemsLoaded = pagination.currentPage >= pagination.totalPages
            val totalCount = obtainTotalCount().plus(searchResponseEntity.animals?.size ?: 0)
            return@map if (searchResponseEntity.animals.isNullOrEmpty()
                && pagination.currentPage == PAGE_ONE
            ) {
                EmptyState(pagination, allItemsLoaded, totalCount)
            } else {
                DefaultState(pagination, allItemsLoaded, totalCount)
            }
        }

    private fun processError(throwable: Throwable): ItemPaginationState =
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

    private fun updateLoadingState(isFirstPage: Boolean): ItemPaginationState =
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
        itemPaginationStateLiveData.value?.paginationEntity ?: PaginationEntity().apply {
            currentPage = 1
        }

    private fun obtainCurrentLoadedAllItems(): Boolean =
        itemPaginationStateLiveData.value?.loadedAllItems ?: false

    private fun obtainTotalCount(): Int = itemPaginationStateLiveData.value?.totalCount ?: 0

}

