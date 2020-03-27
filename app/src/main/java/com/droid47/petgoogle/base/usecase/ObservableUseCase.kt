package com.droid47.petgoogle.base.usecase

import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import com.droid47.petgoogle.base.usecase.executor.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

abstract class ObservableUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseObservable(params: Params? = null): Observable<Results>

    fun execute(observer: DisposableObserver<Results>, params: Params? = null) {
        val observable = buildUseCaseObservableWithSchedulers(params)
    }

    private fun buildUseCaseObservableWithSchedulers(params: Params?): Observable<Results> {
        return buildUseCaseObservable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
