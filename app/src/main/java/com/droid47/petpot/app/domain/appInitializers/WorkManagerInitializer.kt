package com.droid47.petpot.app.domain.appInitializers

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.droid47.petpot.base.appinitializer.AppInitializer
import com.droid47.petpot.workmanagers.di.DaggerWorkerFactory
import javax.inject.Inject

class WorkManagerInitializer @Inject constructor(private val daggerWorkerFactory: DaggerWorkerFactory) :
    AppInitializer {

    override fun init(application: Application) {
        WorkManager.initialize(
            application,
            Configuration.Builder()
                .setWorkerFactory(daggerWorkerFactory)
                .build()
        )
    }
}