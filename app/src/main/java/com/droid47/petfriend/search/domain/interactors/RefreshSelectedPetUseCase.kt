package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import io.reactivex.Completable
import javax.inject.Inject

class RefreshSelectedPetUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petTypeRepository: PetTypeRepository
) : CompletableUseCase<String>(threadExecutor, postExecutionThread) {
    override fun buildUseCaseCompletable(params: String): Completable =
        petTypeRepository.updateSelectedPetTypeRow(params)
}