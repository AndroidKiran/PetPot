package com.droid47.petfriend.bookmark.domain.interactors

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.droid47.petfriend.base.usecase.FlowableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

private const val PAGE_SIZE = 20

class FetchBookmarkListUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val bookmarkRepository: BookmarkRepository
) : FlowableUseCase<BaseStateModel<out PagedList<PetEntity>>, Unit>(
    threadExecutor,
    postExecutionThread
) {
    override fun buildUseCaseObservable(params: Unit?): Flowable<BaseStateModel<out PagedList<PetEntity>>> =
        RxPagedListBuilder(bookmarkRepository.fetchBookmarkList(), PAGE_SIZE)
            .setFetchScheduler(threadExecutorScheduler)
            .setNotifyScheduler(postExecutionThreadScheduler)
            .buildFlowable(BackpressureStrategy.LATEST)
            .map {
                if (it.isEmpty()) {
                    Empty(null)
                } else {
                    Success(it)
                }
            }.onErrorReturn {
                Failure(it, null)
            }
}