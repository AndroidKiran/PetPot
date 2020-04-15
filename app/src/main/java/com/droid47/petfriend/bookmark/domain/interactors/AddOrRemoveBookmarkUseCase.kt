package com.droid47.petfriend.bookmark.domain.interactors

import com.droid47.petfriend.base.usecase.SingleUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Single
import javax.inject.Inject

class AddOrRemoveBookmarkUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val bookmarkRepository: BookmarkRepository
) : SingleUseCase<BaseStateModel<PetEntity>, PetEntity>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: PetEntity?): Single<BaseStateModel<PetEntity>> =
        when (params) {
            null -> Single.just(Failure(IllegalStateException("Params is null")))
            else -> bookmarkRepository.insertOrDeleteBookmark(params)
                .toSingle {
                    Success(params) as BaseStateModel<PetEntity>
                }.onErrorReturn {
                    Failure(it)
                }
        }


}