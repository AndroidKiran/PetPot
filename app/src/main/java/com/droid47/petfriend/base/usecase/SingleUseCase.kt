package com.droid47.petfriend.base.usecase

import com.droid47.petfriend.base.usecase.executor.PostExecutionThread
import com.droid47.petfriend.base.usecase.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.SingleObserver

abstract class SingleUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseSingle(params: Params): Single<Results>

    fun execute(params: Params, observer: SingleObserver<Results>) {
        buildUseCaseSingleWithSchedulers(params).subscribe(observer)
    }

    private fun buildUseCaseSingleWithSchedulers(params: Params): Single<Results> {
        return buildUseCaseSingle(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
