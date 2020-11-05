package com.droid47.petpot.organization.domain

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.droid47.petpot.base.extensions.clearDisposable
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.organization.data.models.OrganizationFilterConstants.PAGE_ONE
import com.droid47.petpot.organization.data.models.OrganizationResponseEntity
import com.droid47.petpot.search.data.models.search.PaginationEntity
import com.droid47.petpot.search.presentation.models.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OrganisationPaginationUseCase @Inject constructor(
    private val fetchAndSaveOrganizationsUseCase: FetchAndSaveOrganizationsUseCase,
    private val subscribeToOrganizationDataSourceUseCase: SubscribeToOrganizationDataSourceUseCase,
    private val organizationFilterUseCase: OrganizationFilterUseCase
) : PagedList.BoundaryCallback<OrganizationCheckableEntity>() {

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

    override fun onItemAtEndLoaded(itemAtEnd: OrganizationCheckableEntity) {
        super.onItemAtEndLoaded(itemAtEnd)
        searchSubject.onNext(false)
    }

    fun buildPageListObservable(
        state: String?,
        name: String?
    ): Flowable<PagedList<OrganizationCheckableEntity>> {
        compositeDisposable.clearDisposable()
        val organizationFilter = organizationFilterUseCase.getOrganizationFilter().apply {
            this.name = if(name.isNullOrEmpty()) null else name
            this.state = if(state.isNullOrEmpty()) null else state
            this.page = PAGE_ONE.toString()
        }
        organizationFilterUseCase.setOrganizationFilter(organizationFilter)
        return subscribeToOrganizationDataSourceUseCase.buildUseCaseObservable(
            Pair(Pair("${name?:""}%", "${state?:""}%"), this)
        )
    }

    fun updatePage(page: Int) {
        val organizationFilter = organizationFilterUseCase.getOrganizationFilter().apply {
            this.page = page.toString()
        }
        organizationFilterUseCase.setOrganizationFilter(organizationFilter)
    }

    @SuppressLint("CheckResult")
    private fun subscribeToSearchSubject() {
        searchSubject.debounce(2, TimeUnit.SECONDS)
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
            val organizationFilter = organizationFilterUseCase.getOrganizationFilter().apply {
                this.page = (if (isFirstPage) PAGE_ONE else this.page.toInt().plus(PAGE_ONE)).toString()
            }
            fetchAndSaveOrganizationsUseCase.buildUseCaseSingle(organizationFilter)
                .doOnSubscribe {
                    _itemPaginationStateLiveData.postValue(updateLoadingState(isFirstPage))
                    compositeDisposable.add(it)
                }.processResponse()
                .onErrorReturn {
                    processError(it)
                }
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

    private fun Single<OrganizationResponseEntity>.processResponse(): Single<ItemPaginationState> =
        map { organizationResponseEntity ->
            val pagination = organizationResponseEntity.paginationEntity ?: PaginationEntity()
            val allItemsLoaded = pagination.currentPage >= pagination.totalPages
            val totalCount =
                obtainTotalCount().plus(organizationResponseEntity.organizationEntities?.size ?: 0)
            return@map if (organizationResponseEntity.organizationEntities.isNullOrEmpty()
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

    private fun obtainPagination(): PaginationEntity =
        itemPaginationStateLiveData.value?.paginationEntity ?: PaginationEntity().apply {
            currentPage = 1
        }

    private fun obtainCurrentLoadedAllItems(): Boolean =
        itemPaginationStateLiveData.value?.loadedAllItems ?: false

    private fun obtainTotalCount(): Int = itemPaginationStateLiveData.value?.totalCount ?: 0

}