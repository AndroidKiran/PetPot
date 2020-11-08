package com.droid47.petpot.petDetails.domain.interactors

import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchFavouritePetFromDbUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : FlowableUseCase<BaseStateModel<FavouritePetEntity>, Int>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseObservable(params: Int): Flowable<BaseStateModel<FavouritePetEntity>> =
        when {
            params < 1 -> Flowable.just(
                Failure(
                    IllegalStateException("Params is null or negative")
                )
            )
            else -> favouritePetRepository.subscribeToSelectedPet(params)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler)
                .map { petEntityList ->
                    if (petEntityList.isNotEmpty()) {
                        Success(petEntityList.first())
                    } else {
                        Empty()
                    }
                }.onErrorReturn {
                    Failure(it)
                }
        }
}