package com.droid47.petpot.base.usecase.executor

import io.reactivex.Scheduler


interface PostExecutionThread {
    val scheduler: Scheduler
}
