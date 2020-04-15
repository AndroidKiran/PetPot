package com.droid47.petfriend.petDetails.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.petDetails.domain.PetDetailsRepository
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Single
import javax.inject.Inject

class FetchDetailsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val petDetailsRepository: PetDetailsRepository
) : SingleUseCase<BaseStateModel<PetEntity>, Int>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Int?): Single<BaseStateModel<PetEntity>> =
        when (params) {
            null -> Single.error(IllegalStateException("Params is null"))
            else -> petDetailsRepository.getPetDetails(params)
                .map { response ->
                    return@map when (response.petEntity) {
                        null -> Failure<PetEntity>(IllegalStateException("Response is null"))
                        else -> Success(response.petEntity)
                    }

                }.onErrorReturn { Failure(it) }
        }
}