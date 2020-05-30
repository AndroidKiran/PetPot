package com.droid47.petfriend.base.usecase.executor

import io.reactivex.Scheduler


interface PostExecutionThread {
    val scheduler: Scheduler
}
