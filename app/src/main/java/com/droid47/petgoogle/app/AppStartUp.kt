package com.droid47.petgoogle.app

import android.content.Context
import com.droid47.petgoogle.BuildConfig
import com.droid47.petgoogle.base.firebase.CrashlyticsExt
import com.facebook.stetho.Stetho

fun Context.initStetho() {
    if (BuildConfig.DEBUG) {
        try {
            Stetho.initializeWithDefaults(this)
        } catch (exception: Exception) {
            CrashlyticsExt.logHandledException(exception)
        }
    }
}