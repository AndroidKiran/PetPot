package com.droid47.petpot.search.domain.interactors

import com.droid47.petpot.base.usecase.FlowableUseCase
import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Empty
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.domain.repositories.FilterRepository
import io.reactivex.Flowable
import javax.inject.Inject

class FetchFilterItemsForSelectedCategoryUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : FlowableUseCase<BaseStateModel<List<PetFilterCheckableEntity>>, String>(
    threadExecutor,
    postExecutionThread
) {

    override fun buildUseCaseObservable(params: String): Flowable<BaseStateModel<List<PetFilterCheckableEntity>>> =
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