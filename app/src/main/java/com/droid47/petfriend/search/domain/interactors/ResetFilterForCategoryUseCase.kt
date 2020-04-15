package com.droid47.petfriend.search.domain.interactors

import android.text.TextUtils
import com.droid47.petfriend.base.usecase.CompletableUseCase
import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import javax.inject.Inject

class ResetFilterForCategoryUseCase @Inject constructor(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread,
    private val filterRepository: FilterRepository
) : CompletableUseCase<String>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseCompletable(params: String?): Completable = when {
        params == null || TextUtils.isEmpty(params) ->
            Completable.error(IllegalStateException("Params is null or empty"))
        else -> filterRepository.updateFilterForCategory(false, params)
    }

}