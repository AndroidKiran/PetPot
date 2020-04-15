package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
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