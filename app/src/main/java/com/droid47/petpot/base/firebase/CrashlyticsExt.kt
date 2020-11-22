package com.droid47.petpot.base.firebase

import com.droid47.petpot.base.extensions.isDeveloperMode
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

object CrashlyticsExt {

    private fun logHandledException(e: Throwable) {
        try {
            Firebase.crashlytics.recordException(e)
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
        Firebase.crashlytics.log(breadCrumb)
    }

    fun crash() {
        throw RuntimeException("Test Crash")
    }
}