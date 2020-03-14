package com.droid47.petgoogle.bookmark.domain.interactors

import com.droid47.petgoogle.base.usecase.CompletableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.bookmark.domain.repositories.BookmarkRepository
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