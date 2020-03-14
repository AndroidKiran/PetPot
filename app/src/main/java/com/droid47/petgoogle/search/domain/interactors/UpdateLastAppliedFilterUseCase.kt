package com.droid47.petgoogle.search.domain.interactors

import com.droid47.petgoogle.base.usecase.CompletableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateLastAppliedFilterUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<List<FilterItemEntity>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: List<FilterItemEntity>?): Completable {
        val filterNameList = params?.map { filterItem -> filterItem.name }?.toList() ?: emptyList()
        return filterRepository.updateLastAppliedFilter(true, filterNameList)
    }
}