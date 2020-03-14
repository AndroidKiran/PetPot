package com.droid47.petgoogle.app.di.workmanager

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class DaggerWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out RxWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): RxWorker? {
        val foundEntry =
            workerFactories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        return foundEntry?.value?.get()?.create(appContext, workerParameters)
    }

    interface ChildWorkerFactory {
        fun create(appContext: Context, params: WorkerParameters): RxWorker
    }
}