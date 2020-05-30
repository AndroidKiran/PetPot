package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import io.reactivex.Single
import javax.inject.Inject

class FetchPetNamesUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petTypeRepository: PetTypeRepository
) : SingleUseCase<List<String>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Unit): Single<List<String>> =
        petTypeRepository.getPetNames()
}