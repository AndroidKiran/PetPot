package com.droid47.petfriend.base.usecase

import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber

abstract class FlowableUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseObservable(params: Params? = null): Flowable<Results>

    fun execute(params: Params? = null, observer: DisposableSubscriber<Results>) {
        buildUseCaseObservableWithSchedulers(params).subscribeWith(observer)
    }

    private fun buildUseCaseObservableWithSchedulers(params: Params?): Flowable<Results> {
        return buildUseCaseObservable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
