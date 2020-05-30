package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Single
import javax.inject.Inject

class UpdateFavoritePetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : SingleUseCase<BaseStateModel<PetEntity>, PetEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: PetEntity): Single<BaseStateModel<PetEntity>> =
        petRepository.updateFavoriteStatus(params)
            .toSingle {
                Success(params) as BaseStateModel<PetEntity>
            }.onErrorReturn {
                Failure(it)
            }
}