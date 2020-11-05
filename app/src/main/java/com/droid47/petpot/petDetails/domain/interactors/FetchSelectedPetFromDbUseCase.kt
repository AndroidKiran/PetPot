package com.droid47.petpot.petDetails.domain.interactors

import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchSelectedPetFromDbUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : FlowableUseCase<BaseStateModel<PetEntity>, Int>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Int): Flowable<BaseStateModel<PetEntity>> =
        when {
            params < 1 -> Flowable.just(
                Failure(
                    IllegalStateException("Params is null or negative")
                )
            )
            else -> petRepository.subscribeToSelectedPet(params)
                .map { petEntity ->
                    Success(petEntity) as BaseStateModel<PetEntity>
                }.onErrorReturn {
                    Failure(it)
                }
        }
}