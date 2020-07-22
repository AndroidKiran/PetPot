package com.droid47.petfriend.organization.domain

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.organization.data.models.OrganizationResponseEntity
import com.droid47.petfriend.organization.data.models.OrganizationFilter
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class FetchAndSaveOrganizationsUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val gson: Gson,
    private val organizationRepository: IOrganizationRepository
) : SingleUseCase<OrganizationResponseEntity, OrganizationFilter>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseSingle(params: OrganizationFilter): Single<OrganizationResponseEntity> {
        return organizationRepository.fetchOrganizationsFromNetwork(params.transformToMap(gson))
            .saveToDb()
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }

    private fun Single<OrganizationResponseEntity>.saveToDb(): Single<OrganizationResponseEntity> {
        return flatMap { organisationResponseEntity ->
            organizationRepository.addOrganizationsToDb(
                organisationResponseEntity.organizationEntities ?: emptyList()
            ).map {
                organisationResponseEntity
            }
        }
    }
}