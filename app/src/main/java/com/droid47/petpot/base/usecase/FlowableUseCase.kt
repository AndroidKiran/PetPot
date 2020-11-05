package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Flowable

abstract class FlowableUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseObservable(params: Params): Flowable<Results>

//    fun execute(params: Params, observer: DisposableSubscriber<Results>) {
//    }
//
//    private fun buildUseCaseObservableWithSchedulers(params: Params): Flowable<Results> {
//        return buildUseCaseObservable(params)
//            .subscribeOn(threadExecutorScheduler)
//            .observeOn(postExecutionThreadScheduler)
//    }
}
