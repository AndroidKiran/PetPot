package com.droid47.petfriend.organization.domain

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateOrganizationUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val organizationRepository: IOrganizationRepository
) : CompletableUseCase<OrganizationCheckableEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: OrganizationCheckableEntity): Completable {
        return organizationRepository.updateOrganization(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}