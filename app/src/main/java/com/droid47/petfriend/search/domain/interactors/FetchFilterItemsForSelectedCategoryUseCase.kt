package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
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

    override fun buildUseCaseObservable(params: String): Flowable<BaseStateModel<List<FilterItemEntity>>> =
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