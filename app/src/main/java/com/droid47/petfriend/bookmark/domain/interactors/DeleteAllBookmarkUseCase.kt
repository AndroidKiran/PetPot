package com.droid47.petfriend.bookmark.domain.interactors

import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteAllBookmarkUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val bookmarkRepository: BookmarkRepository
) : CompletableUseCase<Unit>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: Unit?): Completable =
        bookmarkRepository.deleteAllBookmark()
}