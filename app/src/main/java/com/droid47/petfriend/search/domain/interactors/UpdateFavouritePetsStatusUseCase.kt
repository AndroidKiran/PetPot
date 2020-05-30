package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFavouritePetsStatusUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : CompletableUseCase<Pair<Boolean, Boolean>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Pair<Boolean, Boolean>): Completable =
        petRepository.updateFavoritePetStatus(params.first, params.second)
}