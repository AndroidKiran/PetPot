package com.droid47.petgoogle.base.firebase

import com.droid47.petgoogle.base.extensions.LoggerExt
import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsExt {

    private fun getCrashlytics() = FirebaseCrashlytics.getInstance()

    fun logHandledException(e: Throwable) {
        if (!LoggerExt.isDeveloperMode()) {
            try {
                getCrashlytics()
                    .recordException(e)
            } catch (e1: Exception) {
            }
        } else {
            e.printStackTrace()
        }
    }

    fun handleException(e: Throwable) {
        if (LoggerExt.isDeveloperMode()) {
            e.printStackTrace()
        } else {
            logHandledException(
                e
            )
        }
    }

    fun dropBreadCrumb(
        category: String?,
        action: String?,
        value: String?
    ) {
        val breadCrumb =
            String.format("%s | %s | %s", category, action, value)
        getCrashlytics().log(breadCrumb)
    }

    fun setCollectionEnabled(status: Boolean) {
        getCrashlytics().setCrashlyticsCollectionEnabled(status)
    }
}