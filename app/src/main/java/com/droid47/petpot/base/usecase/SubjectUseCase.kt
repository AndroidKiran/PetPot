package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

abstract class SubjectUseCase<Results, Params>(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : BaseReactiveUseCase(threadExecutor, postExecutionThread) {

    val saveLocationSubject: Subject<Params> = PublishSubject.create<Params>().toSerialized()

    abstract fun buildUseCaseSubject(observer: Observer<Results>): Subject<Params>

}
