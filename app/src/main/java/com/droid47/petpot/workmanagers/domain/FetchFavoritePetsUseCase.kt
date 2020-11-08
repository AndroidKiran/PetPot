package com.droid47.petpot.workmanagers.domain

import com.droid47.petpot.base.usecase.SingleUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import io.reactivex.Single
import javax.inject.Inject

class FetchFavoritePetsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val favouritePetRepository: FavouritePetRepository
) : SingleUseCase<BaseStateModel<List<FavouritePetEntity>>, Unit>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseSingle(params: Unit): Single<BaseStateModel<List<FavouritePetEntity>>> =
        favouritePetRepository.fetchFavoritePets()
            .map { petList ->
                when {
                    petList.isEmpty() -> Empty(petList)
                    else -> Success(petList.distinctBy { favouritePetEntity -> favouritePetEntity.id })
                }
            }.onErrorReturn { Failure(it, emptyList()) }
}