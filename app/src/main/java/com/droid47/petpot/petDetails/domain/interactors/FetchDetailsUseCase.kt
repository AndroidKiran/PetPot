package com.droid47.petpot.petDetails.domain.interactors

import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.petDetails.domain.PetDetailsRepository
import com.droid47.petpot.search.data.models.search.PetEntity
import io.reactivex.Single
import javax.inject.Inject

class FetchDetailsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petDetailsRepository: PetDetailsRepository
) : SingleUseCase<BaseStateModel<PetEntity>, Int>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Int): Single<BaseStateModel<PetEntity>> =
        petDetailsRepository.getPetDetails(params)
            .map { response ->
                return@map when (response.petEntity) {
                    null -> Failure<PetEntity>(IllegalStateException("Response is null"))
                    else -> Success(response.petEntity)
                }
            }.onErrorReturn { Failure(it) }

}