package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.SingleObserver
import java.util.concurrent.TimeUnit

abstract class SingleUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseSingle(params: Params): Single<Results>

    fun execute(params: Params, observer: SingleObserver<Results>) {
        buildUseCaseSingleWithSchedulers(params).subscribe(observer)
    }

    fun execute(params: Params, delay:Long, timeUnit: TimeUnit, observer: SingleObserver<Results>) {
        buildUseCaseSingleWithSchedulers(params)
            .delay(delay, timeUnit)
            .subscribe(observer)
    }

    fun buildUseCaseSingleWithSchedulers(params: Params): Single<Results> {
        return buildUseCaseSingle(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
