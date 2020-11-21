package com.droid47.petpot.search.domain.interactors

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_SIZE
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToPetDataSourceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository,
    private val favouritePetRepository: FavouritePetRepository
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
                if (it.isNullOrEmpty()) {
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
            is DataSourceType.FavoriteType -> favouritePetRepository.fetchFavoritePetsFromDB(true)
            is DataSourceType.NonFavoriteType -> favouritePetRepository.fetchFavoritePetsFromDB(
                false
            )
            else -> petRepository.fetchAllPetsFromDb()
        }
}

sealed class DataSourceType {
    object DistanceType : DataSourceType()
    object RecentType : DataSourceType()
    object FavoriteType : DataSourceType()
    object NonFavoriteType : DataSourceType()
    object AllType : DataSourceType()
}
