package com.droid47.petfriend.petDetails.domain.interactors

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.repositories.PetRepository
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