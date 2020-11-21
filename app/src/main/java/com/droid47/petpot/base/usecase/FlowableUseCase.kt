package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import java.util.concurrent.TimeUnit

abstract class FlowableUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseObservable(params: Params): Flowable<Results>

    fun execute(observer: FlowableSubscriber<Results>, params: Params) {
        buildUseCaseObservableWithSchedulers(params).subscribe(observer)
    }

    fun execute(
        params: Params,
        delay: Long,
        timeUnit: TimeUnit,
        observer: FlowableSubscriber<Results>
    ) {
        buildUseCaseObservableWithSchedulers(params)
            .delay(delay, timeUnit)
            .subscribe(observer)
    }

    fun buildUseCaseObservableWithSchedulers(params: Params): Flowable<Results> {
        return buildUseCaseObservable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
