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

class FetchFavoriteStateUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : FlowableUseCase<BaseStateModel<PetEntity>, Int>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Int?): Flowable<BaseStateModel<PetEntity>> =
        when {
            params == null || params < 1 -> Flowable.just(
                Failure(
                    IllegalStateException("Params is null or negative")
                )
            )
            else -> petRepository.subscribeToUpdate(params)
                .map { petList ->
                    when {
                        petList.isEmpty() ->
                            Failure<PetEntity>(IllegalStateException("Params is null or negative"))
                        else -> Success(petList[0])
                    }
                }.onErrorReturn {
                    Failure(it)
                }
        }
}