package com.droid47.petpot.search.domain.interactors

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petpot.base.extensions.applyIOSchedulers
import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.presentation.models.PetFilterConstants.PAGE_SIZE
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToFavouritePetDataSourceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : FlowableUseCase<BaseStateModel<out PagedList<FavouritePetEntity>>, Pair<DataSourceType, String>>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: Pair<DataSourceType, String>): Flowable<BaseStateModel<out PagedList<FavouritePetEntity>>> =
        RxPagedListBuilder(
            favouritePetRepository.fetchFavoritePetsFromDB(),
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
}

