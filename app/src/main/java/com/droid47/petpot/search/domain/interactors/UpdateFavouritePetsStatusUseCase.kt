package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFavouritePetsStatusUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : CompletableUseCase<Pair<Boolean, Boolean>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Pair<Boolean, Boolean>): Completable =
        favouritePetRepository.updateFavoritePetStatus(params.first, params.second)
}