package com.droid47.petpot.search.domain.interactors

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.domain.repositories.PetRepository
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_SIZE
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToPetDataSourceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : FlowableUseCase<BaseStateModel<out PagedList<SearchPetEntity>>, Pair<DataSourceType, String>>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: Pair<DataSourceType, String>): Flowable<BaseStateModel<out PagedList<SearchPetEntity>>> =
        RxPagedListBuilder(
            getDataSourceType(params.first, params.second),
            PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(false)
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

    private fun getDataSourceType(
        dataSourceType: DataSourceType,
        query: String
    ): DataSource.Factory<Int, SearchPetEntity> =
        when (dataSourceType) {
            is DataSourceType.DistanceType -> petRepository.fetchNearByPetsFromDb(query)
            is DataSourceType.RecentType -> petRepository.fetchRecentPetsFromDB(query)
            else -> petRepository.fetchAllPetsFromDb()
        }
}

sealed class DataSourceType {
    object DistanceType : DataSourceType()
    object RecentType : DataSourceType()
    object FavoriteType : DataSourceType()
    object AllType : DataSourceType()
}
