package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Completable
import io.reactivex.Single
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