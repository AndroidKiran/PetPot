package com.droid47.petpot.organization.domain

import com.droid47.petpot.base.usecase.CompletableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.organization.domain.repositories.IOrganizationRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateOrganizationUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val organizationRepository: IOrganizationRepository
) : CompletableUseCase<OrganizationCheckableEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: OrganizationCheckableEntity): Completable {
        return organizationRepository.updateOrganization(params)
    }
}