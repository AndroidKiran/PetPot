package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import java.util.concurrent.TimeUnit

abstract class ObservableUseCase<Results, in Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    abstract fun buildUseCaseObservable(params: Params? = null): Observable<Results>

    fun execute(observer: DisposableObserver<Results>, params: Params? = null) {
        buildUseCaseObservableWithSchedulers(params).subscribe(observer)
    }

    fun execute(
        params: Params,
        delay: Long,
        timeUnit: TimeUnit,
        observer: DisposableObserver<Results>
    ) {
        buildUseCaseObservableWithSchedulers(params)
            .delay(delay, timeUnit)
            .subscribe(observer)
    }

    fun buildUseCaseObservableWithSchedulers(params: Params?): Observable<Results> {
        return buildUseCaseObservable(params)
            .subscribeOn(threadExecutorScheduler)
            .observeOn(postExecutionThreadScheduler)
    }
}
