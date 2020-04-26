package com.droid47.petfriend.bookmark.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Single
import javax.inject.Inject

class RemoveAllPetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : SingleUseCase<Int, Boolean>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Boolean?): Single<Int> =
        petRepository.clearPetsFromDb(params ?: true)
}