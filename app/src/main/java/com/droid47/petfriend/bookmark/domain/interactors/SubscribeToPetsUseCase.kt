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
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_SIZE
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToPetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : FlowableUseCase<BaseStateModel<out PagedList<PetEntity>>, Pair<DataSourceType, String>>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: Pair<DataSourceType, String>): Flowable<BaseStateModel<out PagedList<PetEntity>>> =
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

    private fun getDataSourceType(dataSourceType: DataSourceType, query: String) =
        when (dataSourceType) {
            is DataSourceType.DistanceType -> petRepository.fetchNearByPetsFromDb(query)
            is DataSourceType.RecentType -> petRepository.fetchRecentPetsFromDB(query)
            is DataSourceType.FavoriteType -> petRepository.fetchFavoritePetsFromDB(true)
            is DataSourceType.NonFavoriteType -> petRepository.fetchFavoritePetsFromDB(false)
            else -> petRepository.fetchAllPetsFromDb()
        }
}

sealed class DataSourceType {
    object DistanceType : DataSourceType()
    object RecentType : DataSourceType()
    object FavoriteType : DataSourceType()
    object NonFavoriteType : DataSourceType()
    object AllType: DataSourceType()
}
