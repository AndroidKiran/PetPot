package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchSelectedFiltersForCategoriesUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : FlowableUseCase<BaseStateModel<List<FilterItemEntity>>, List<String>>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseObservable(params: List<String>): Flowable<BaseStateModel<List<FilterItemEntity>>> =
        filterRepository.getSelectedFilterItemsForCategories(params)
            .map { filterItems ->
                return@map when {
                    filterItems.isEmpty() -> Empty(filterItems)
                    else -> Success(filterItems)
                }
            }
}