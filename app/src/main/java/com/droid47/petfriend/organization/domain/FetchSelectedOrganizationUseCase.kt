package com.droid47.petfriend.organization.domain

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchSelectedOrganizationUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val organizationRepository: IOrganizationRepository
) : FlowableUseCase<BaseStateModel<List<OrganizationCheckableEntity>>, Boolean>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseObservable(params: Boolean): Flowable<BaseStateModel<List<OrganizationCheckableEntity>>> {
        return organizationRepository.getSelectedOrganizations(params)
            .map {
                return@map if (it.isEmpty()) {
                    Empty<List<OrganizationCheckableEntity>>()
                } else {
                    Success(it)
                }
            }.onErrorReturn {
                Failure(it)
            }
    }
}