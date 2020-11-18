package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFilterOnAppliedUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<List<String>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: List<String>): Completable {
        return filterRepository.updateFilterOnApplied(params)
    }
}