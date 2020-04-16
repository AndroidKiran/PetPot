package com.droid47.petfriend.app

import android.app.Application
import com.bumptech.glide.Glide
import com.droid47.petfriend.app.di.components.AppComponent
import com.droid47.petfriend.app.di.components.DaggerAppComponent
import com.droid47.petfriend.base.extensions.applyTheme
import com.droid47.petfriend.base.firebase.CrashlyticsExt

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
        applyTheme(appComponent.sharedPreference().fetchAppliedTheme())
    }
}