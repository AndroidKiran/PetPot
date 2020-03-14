package com.droid47.petgoogle.search.domain.interactors

import android.text.TextUtils
import com.droid47.petgoogle.base.usecase.CompletableUseCase
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import com.droid47.petgoogle.search.domain.repositories.SearchRepository
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