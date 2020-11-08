package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.domain.repositories.PetTypeRepository
import io.reactivex.Completable
import javax.inject.Inject

class RefreshSelectedPetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petTypeRepository: PetTypeRepository
) : CompletableUseCase<String>(threadExecutor, postExecutionThread) {
    override fun buildUseCaseCompletable(params: String): Completable =
        petTypeRepository.updateSelectedPetTypeRow(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
}