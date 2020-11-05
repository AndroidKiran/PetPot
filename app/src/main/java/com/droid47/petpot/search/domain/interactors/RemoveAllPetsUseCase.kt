package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Completable
import javax.inject.Inject

class RemoveAllPetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : CompletableUseCase<Boolean>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Boolean): Completable =
        petRepository.clearPetsFromDb(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
}