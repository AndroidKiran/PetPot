package com.droid47.petgoogle.bookmark.domain.interactors

import com.droid47.petgoogle.base.usecase.FlowableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Empty
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.Success
import com.droid47.petgoogle.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petgoogle.search.data.models.search.PetEntity
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