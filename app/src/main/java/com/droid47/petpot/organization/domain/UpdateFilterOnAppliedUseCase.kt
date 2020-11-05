package com.droid47.petpot.organization.domain

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.organization.domain.repositories.IOrganizationRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFilterOnAppliedUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val organizationFilterRepository: IOrganizationRepository
) : CompletableUseCase<Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Unit): Completable {
        return organizationFilterRepository.updateFilterOnApplied()
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}