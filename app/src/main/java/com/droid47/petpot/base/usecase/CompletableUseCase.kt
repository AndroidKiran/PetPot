package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import java.util.concurrent.TimeUnit

abstract class CompletableUseCase<in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseCompletable(params: Params): Completable

    fun execute(params: Params, observer: CompletableObserver) {
        buildUseCaseCompletableWithSchedulers(params).subscribe(observer)
    }

    fun execute(params: Params, delay: Long, timeUnit: TimeUnit, observer: CompletableObserver) {
        buildUseCaseCompletableWithSchedulers(params)
            .delay(delay, timeUnit)
            .subscribe(observer)
    }

    fun buildUseCaseCompletableWithSchedulers(params: Params): Completable {
        return buildUseCaseCompletable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
