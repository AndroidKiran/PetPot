package com.droid47.petgoogle.search.domain.interactors

import com.droid47.petgoogle.base.usecase.CompletableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class ApplyFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<FilterItemEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: FilterItemEntity?): Completable = when (params) {
        null -> Completable.error(IllegalStateException("Params is null or empty"))
        else -> filterRepository.updateOrInsertTheFilter(params)
    }

}