package com.droid47.petgoogle.base.usecase

import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.CompletableObserver

abstract class CompletableUseCase<in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseCompletable(params: Params? = null): Completable

    fun execute(params: Params? = null, observer: CompletableObserver) {
        buildUseCaseCompletableWithSchedulers(params).subscribe(observer)
    }

    private fun buildUseCaseCompletableWithSchedulers(params: Params?): Completable {
        return buildUseCaseCompletable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
