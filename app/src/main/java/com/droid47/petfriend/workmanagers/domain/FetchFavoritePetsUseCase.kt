package com.droid47.petfriend.workmanagers.domain

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Single
import javax.inject.Inject

class FetchFavoritePetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petRepository: PetRepository
) : SingleUseCase<BaseStateModel<List<PetEntity>>, Boolean>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Boolean): Single<BaseStateModel<List<PetEntity>>> =
        petRepository.fetchFavoritePets(params)
            .map { petList ->
                when {
                    petList.isEmpty() -> Empty(petList)
                    else -> Success(petList.distinctBy { petEntity -> petEntity.id })
                }
            }.onErrorReturn { Failure(it, emptyList()) }
}