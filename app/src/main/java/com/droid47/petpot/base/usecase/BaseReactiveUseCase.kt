package com.droid47.petpot.base.usecase

import com.droid47.petpot.base.usecase.executor.PostExecutionThread
import com.droid47.petpot.base.usecase.executor.ThreadExecutor
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


abstract class BaseReactiveUseCase(
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) {

    protected val threadExecutorScheduler: Scheduler = Schedulers.from(threadExecutor)
    protected val postExecutionThreadScheduler: Scheduler = postExecutionThread.scheduler

}
