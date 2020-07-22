package com.droid47.petfriend.app.domain.appInitializers

import android.app.Application
import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.base.appinitializer.AppInitializer
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.facebook.stetho.Stetho
import javax.inject.Inject

class StethoInitializer @Inject constructor(): AppInitializer {

    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            try {
                Stetho.initializeWithDefaults(application)
            } catch (exception: Exception) {
                CrashlyticsExt.handleException(exception)
            }
        }
    }
}