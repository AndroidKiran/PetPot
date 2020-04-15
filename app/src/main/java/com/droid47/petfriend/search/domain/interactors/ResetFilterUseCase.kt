package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class ResetFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<List<String>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: List<String>?): Completable = when {
        params == null || params.isEmpty() ->
            Completable.error(IllegalStateException("Params is null or empty"))
        else -> filterRepository.resetFilter(params)
    }

}