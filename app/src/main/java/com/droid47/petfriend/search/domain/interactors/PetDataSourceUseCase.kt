package com.droid47.petfriend.search.domain.interactors

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petfriend.base.extensions.log
import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.LOCATION
import com.droid47.petfriend.search.data.models.PET_TYPE
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import com.droid47.petfriend.search.domain.repositories.PetRepository
import com.droid47.petfriend.search.presentation.models.FilterConstants
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class PetDataSourceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository,
    private val filterRepository: FilterRepository
) : FlowableUseCase<BaseStateModel<PagedList<PetEntity>>, PetPaginationUseCase>(
    threadExecutor,
    postExecutionThread
) {

//    override fun buildUseCaseObservable(params: Triple<String, String, PetPaginationUseCase>): Flowable<BaseStateModel<PagedList<PetEntity>>>  =
//        RxPagedListBuilder(
//            getDataSourceType(params.first, params.second),
//            PagedList.Config.Builder()
//                .setPageSize(FilterConstants.PAGE_SIZE)
//                .setPrefetchDistance(3)
//                .setInitialLoadSizeHint(1)
//                .build()
//        ).setFetchScheduler(threadExecutorScheduler)
//            .setNotifyScheduler(postExecutionThreadScheduler)
//            .setBoundaryCallback(params.third)
//            .buildFlowable(BackpressureStrategy.MISSING)
//            .map {
//                when {
//                    it.isEmpty() -> Empty(it)
//                    else -> Success(it)
//                }
//            }.onErrorReturn {
//                Failure(it, null)
//            }

    override fun buildUseCaseObservable(params: PetPaginationUseCase): Flowable<BaseStateModel<PagedList<PetEntity>>> =
        filterRepository.getFilterForTypes(listOf(PET_TYPE, LOCATION), true)
            .distinctUntilChanged()
            .reduceToPair()
            .createPagedListBuilder(params)

    private fun Flowable<List<FilterItemEntity>>.reduceToPair(): Flowable<Pair<String, String>> =
        map {
            var petName = ""
            var location = ""
            it.forEach { filterItemEntity ->
                if (filterItemEntity.type == PET_TYPE) {
                    petName = filterItemEntity.name
                }
                if (filterItemEntity.type == LOCATION) {
                    location = filterItemEntity.name
                }
            }
            Pair(location, petName)
        }

    private fun Flowable<Pair<String, String>>.createPagedListBuilder(petPaginationUseCase: PetPaginationUseCase): Flowable<BaseStateModel<PagedList<PetEntity>>> =
        switchMap { pair ->
            RxPagedListBuilder(
                getDataSourceType(pair.first, pair.second),
                PagedList.Config.Builder()
                    .setPageSize(FilterConstants.PAGE_SIZE)
                    .setEnablePlaceholders(true)
                    .setPrefetchDistance(5)
                    .setInitialLoadSizeHint(1)
                    .build()
            ).setFetchScheduler(threadExecutorScheduler)
                .setNotifyScheduler(postExecutionThreadScheduler)
                .setBoundaryCallback(petPaginationUseCase)
                .buildFlowable(BackpressureStrategy.MISSING)
                .map {
                    when {
                        it.isEmpty() -> Empty(it)
                        else -> Success(it)
                    }
                }.onErrorReturn {
                    Failure(it, null)
                }
        }

    private fun getDataSourceType(
        location: String?,
        query: String
    ): DataSource.Factory<Int, PetEntity> =
        if (location.isNullOrEmpty()) {
            petRepository.fetchRecentPetsFromDB(query)
        } else {
            petRepository.fetchNearByPetsFromDb(query)
        }
}