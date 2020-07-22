package com.droid47.petfriend.organization.domain

import android.app.Application
import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.organization.data.models.State
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class FetchStatesUseCase @Inject constructor(
    threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
    private val application: Application,
    private val gson: Gson,
    private val organizationRepository: IOrganizationRepository
) : FlowableUseCase<List<State>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Unit): Flowable<List<State>> {
        return organizationRepository.getStates(gson, application)
    }

}