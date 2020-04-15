package com.droid47.petfriend.bookmark.domain.interactors

import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Flowable
import javax.inject.Inject

class FetchBookmarkListUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val bookmarkRepository: BookmarkRepository
) : FlowableUseCase<BaseStateModel<List<PetEntity>>, Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Unit?): Flowable<BaseStateModel<List<PetEntity>>> =
        bookmarkRepository.fetchBookmarkList()
            .map { petList ->
                when {
                    petList.isEmpty() -> Empty(petList)
                    else -> Success(petList.distinctBy { petEntity -> petEntity.id })
                }
            }.onErrorReturn { Failure(it, emptyList()) }
}