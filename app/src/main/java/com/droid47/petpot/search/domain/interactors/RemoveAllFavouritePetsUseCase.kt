package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Completable
import javax.inject.Inject

class RemoveAllFavouritePetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : CompletableUseCase<Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Unit): Completable =
        favouritePetRepository.deleteAllFavouritePet()
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
}