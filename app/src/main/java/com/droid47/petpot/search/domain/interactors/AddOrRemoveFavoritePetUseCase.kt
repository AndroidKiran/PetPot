package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Single
import javax.inject.Inject

class AddOrRemoveFavoritePetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : SingleUseCase<BaseStateModel<FavouritePetEntity>, FavouritePetEntity>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseSingle(params: FavouritePetEntity): Single<BaseStateModel<FavouritePetEntity>> =
        when (params.bookmarkStatus) {
            true -> favouritePetRepository.addFavouritePet(params)
            false -> favouritePetRepository.deleteFavouritePet(params)
        }.subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
            .toSingle() {
                Success(params) as BaseStateModel<FavouritePetEntity>
            }.onErrorReturn {
                Failure(it)
            }
}