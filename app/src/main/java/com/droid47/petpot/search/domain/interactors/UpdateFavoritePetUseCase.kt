package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import io.reactivex.Single
import javax.inject.Inject

class UpdateFavoritePetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : SingleUseCase<BaseStateModel<PetEntity>, PetEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: PetEntity): Single<BaseStateModel<PetEntity>> =
        favouritePetRepository.updateFavoriteStatus(params)
            .toSingle {
                Success(params) as BaseStateModel<PetEntity>
            }.onErrorReturn {
                Failure(it)
            }
}