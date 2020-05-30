package com.droid47.petfriend.base.usecase

import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.CompletableObserver

abstract class CompletableUseCase<in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseCompletable(params: Params): Completable

    fun execute(params: Params, observer: CompletableObserver) {
        buildUseCaseCompletableWithSchedulers(params).subscribe(observer)
    }

    private fun buildUseCaseCompletableWithSchedulers(params: Params): Completable {
        return buildUseCaseCompletable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
