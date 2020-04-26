package com.droid47.petfriend.bookmark.domain.interactors

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

private const val PAGE_SIZE = 20

class SubscribeToPetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : FlowableUseCase<BaseStateModel<out PagedList<PetEntity>>, DataSourceType>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: DataSourceType?): Flowable<BaseStateModel<out PagedList<PetEntity>>> =
        RxPagedListBuilder(
            getDataSourceType(params ?: DataSourceType.RecentType), PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .build()
        )
            .setFetchScheduler(threadExecutorScheduler)
            .setNotifyScheduler(postExecutionThreadScheduler)
            .buildFlowable(BackpressureStrategy.LATEST)
            .map {
                if (it.isEmpty()) {
                    Empty(null)
                } else {
                    Success(it)
                }
            }.onErrorReturn {
                Failure(it, null)
            }

    private fun getDataSourceType(dataSourceType: DataSourceType) =
        when (dataSourceType) {
            is DataSourceType.DistanceType -> petRepository.fetchNearByPetsFromDb(false)
            is DataSourceType.RecentType -> petRepository.fetchRecentPetsFromDB(false)
            else -> petRepository.fetchFavoritePetsFromDB(true)
        }
}

sealed class DataSourceType {
    object DistanceType : DataSourceType()
    object RecentType : DataSourceType()
    object FavoriteType : DataSourceType()
}
