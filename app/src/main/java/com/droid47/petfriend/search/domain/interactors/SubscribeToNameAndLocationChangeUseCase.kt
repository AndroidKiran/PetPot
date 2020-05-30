package com.droid47.petfriend.search.domain.interactors

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.data.models.LOCATION
import com.droid47.petfriend.search.data.models.PET_TYPE
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Flowable
import javax.inject.Inject

class SubscribeToNameAndLocationChangeUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
): FlowableUseCase<Pair<String, String>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Unit): Flowable<Pair<String, String>> =
        filterRepository.getFilterForTypes(listOf(PET_TYPE, LOCATION), true)
            .distinctUntilChanged()
            .map {
                var petName = ""
                var location = ""
                it.forEach { filterItemEntity ->
                    if (filterItemEntity.type == PET_TYPE) {
                        petName = filterItemEntity.name
                    }
                    if (filterItemEntity.type == LOCATION) {
                        location = filterItemEntity.name
                    }
                }
                Pair(location, petName)
            }
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
}