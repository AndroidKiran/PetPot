package com.droid47.petgoogle.search.domain.interactors

import com.droid47.petgoogle.base.usecase.FlowableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Empty
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.Success
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchFilterItemsForSelectedCategoryUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : FlowableUseCase<BaseStateModel<List<FilterItemEntity>>, String>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseObservable(params: String?): Flowable<BaseStateModel<List<FilterItemEntity>>> =
        if (params == null) {
            Flowable.just(Failure(IllegalStateException("Category is null"), emptyList()))
        } else {
            filterRepository.getFilterItemsForSelectedCategory(params)
                .map { filterItems ->
                    return@map if (filterItems.isEmpty()) {
                        Empty(filterItems)
                    } else {
                        Success(filterItems)
                    }
                }.onErrorReturn { throwable ->
                    Failure(throwable, emptyList())
                }
        }
}