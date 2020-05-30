package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.LOCATION
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<FilterItemEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: FilterItemEntity): Completable =
        when (params.type) {

            LOCATION -> filterRepository.updateLocationFilter(params)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler)

            else -> filterRepository.updateOrInsertTheFilter(params)
                .subscribeOn(threadExecutorScheduler)
                .observeOn(postExecutionThreadScheduler)
        }
}