package com.droid47.petgoogle.search.domain.interactors

import com.droid47.petgoogle.base.usecase.SingleUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.Success
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.data.models.SORT
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import io.reactivex.Single
import javax.inject.Inject

class FetchSortMenuStateUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : SingleUseCase<BaseStateModel<FilterItemEntity>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Unit?): Single<BaseStateModel<FilterItemEntity>> =
        filterRepository.getFilterItemForCategory(SORT)
            .map { filterItem ->
                when (filterItem) {
                    null -> Failure<FilterItemEntity>(IllegalStateException("Result is null"))
                    else -> Success(filterItem)
                }
            }.onErrorReturn {
                Failure(it)
            }
}