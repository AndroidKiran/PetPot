package com.droid47.petgoogle.app

import android.app.Application
import com.bumptech.glide.Glide
import com.droid47.petgoogle.app.di.components.AppComponent
import com.droid47.petgoogle.app.di.components.DaggerAppComponent
import com.droid47.petgoogle.base.extensions.applyTheme

class PetApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this@PetApplication)
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