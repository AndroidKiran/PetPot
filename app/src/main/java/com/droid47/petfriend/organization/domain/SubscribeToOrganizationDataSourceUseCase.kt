package com.droid47.petfriend.organization.domain

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.data.models.OrganizationFilterConstants.INITIAL_LOAD_SIZE_HINT
import com.droid47.petfriend.organization.data.models.OrganizationFilterConstants.PAGE_SIZE
import com.droid47.petfriend.organization.data.models.OrganizationFilterConstants.PRE_FETCH_DISTANCE
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToOrganizationDataSourceUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val organizationRepository: IOrganizationRepository
) : FlowableUseCase<PagedList<OrganizationCheckableEntity>, Pair<Pair<String, String>, OrganisationPaginationUseCase>>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: Pair<Pair<String, String>, OrganisationPaginationUseCase>): Flowable<PagedList<OrganizationCheckableEntity>> {
        val name = params.first.first
        val state = params.first.second
        return RxPagedListBuilder(
            organizationRepository.fetchOrganizationsFromDb(name, state),
            PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PRE_FETCH_DISTANCE)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                .setEnablePlaceholders(true)
                .build()
        ).setBoundaryCallback(params.second)
            .setFetchScheduler(threadExecutorScheduler)
            .setNotifyScheduler(postExecutionThreadScheduler)
            .buildFlowable(BackpressureStrategy.LATEST)
    }
}