package com.droid47.petgoogle.app.domain

import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import com.droid47.petgoogle.base.usecase.executor.PostExecutionThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@ApplicationScope
class UIThread @Inject constructor() : PostExecutionThread {

    override val scheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
}
