package com.droid47.petgoogle.app

import com.bumptech.glide.Glide
import com.droid47.petgoogle.app.di.DaggerAppComponent
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.extensions.applyTheme
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class PetApplication : DaggerApplication() {

    @Inject
    lateinit var localSharedPreferences: LocalPreferencesRepository

    private val appComponent: AndroidInjector<PetApplication> by lazy {
        DaggerAppComponent.builder().create(this@PetApplication)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this@PetApplication
        enableTheme()
        initStetho()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    private fun enableTheme() {
        applyTheme(localSharedPreferences.fetchAppliedTheme())
    }

    companion object {
        lateinit var instance: PetApplication
            private set
    }
}