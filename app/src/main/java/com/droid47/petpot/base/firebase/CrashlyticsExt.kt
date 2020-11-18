package com.droid47.petpot.base.firebase

import com.droid47.petpot.base.extensions.isDeveloperMode
import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsExt {

    private fun getCrashlytics() = FirebaseCrashlytics.getInstance()

    private fun logHandledException(e: Throwable) {
        try {
            getCrashlytics()
                .recordException(e)
        } catch (e1: Exception) {
        }
    }

    fun handleException(e: Throwable) {
        if (isDeveloperMode()) {
            e.printStackTrace()
        } else {
            logHandledException(e)
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

    fun crash() {
        throw RuntimeException("Test Crash")
    }
}