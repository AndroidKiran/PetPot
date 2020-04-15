package com.droid47.petfriend.app

import android.content.Context
import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.base.firebase.CrashlyticsExt
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