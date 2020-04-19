package com.droid47.petfriend.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.droid47.petfriend.app.di.components.AppComponent
import com.droid47.petfriend.app.di.components.DaggerAppComponent
import com.droid47.petfriend.base.extensions.applyTheme
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.workmanagers.di.DaggerWorkerFactory
import javax.inject.Inject

class PetApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this@PetApplication)
    }

    init {
        try {
            System.loadLibrary("native-lib");
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
        }
    }

    override fun onCreate() {
        super.onCreate()
        enableTheme()
        initStetho()
        initWorkManager()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    private fun enableTheme() {
        appComponent.sharedPreference().fetchAppliedTheme().applyTheme()
    }

    private fun initWorkManager() {
        WorkManager.initialize(this,
            Configuration.Builder().setWorkerFactory(appComponent.daggerWorkerFactory()).build())
    }
}