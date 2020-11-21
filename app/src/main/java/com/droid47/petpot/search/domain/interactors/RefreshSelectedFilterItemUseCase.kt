package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class RefreshSelectedFilterItemUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<PetFilterCheckableEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: PetFilterCheckableEntity): Completable =
        filterRepository.updateFilterItem(params)

}