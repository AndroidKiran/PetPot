package com.droid47.petpot.app.domain

import android.app.Activity
import android.app.Application
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.perf.ktx.performance
import javax.inject.Inject

class FirebaseManager @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : IFirebaseManager {

    override fun setCollectionEnabled() {
        val tncStatus = localPreferencesRepository.getTnCState()
        setCollectionEnabled(tncStatus)
        setUserId(tncStatus)
    }

    override fun setCollectionDisabled() {
        setCollectionEnabled(false)
    }

    override fun sendScreenView(screenName: String, className: String, activity: Activity) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_SCREENVIEW)
        }
    }

    override fun logUiEvent(itemId: String, action: String) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, itemId)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_UI_EVENT)
            param(FA_KEY_UI_ACTION, action)
        }
    }

    private fun setCollectionEnabled(status: Boolean) {
        Firebase.analytics.setAnalyticsCollectionEnabled(status)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(status)
        Firebase.performance.isPerformanceCollectionEnabled = status
        Firebase.messaging.isAutoInitEnabled = status
    }

    private fun setUserId(tncStatus: Boolean) {
        if (!tncStatus) return
        val instanceId = localPreferencesRepository.getFcmToken() ?: return
        Firebase.analytics.setUserId(instanceId)
        Firebase.crashlytics.setUserId(instanceId)
    }

    companion object {
        private const val FA_CONTENT_TYPE_SCREENVIEW = "screen"
        private const val FA_KEY_UI_ACTION = "ui_action"
        private const val FA_CONTENT_TYPE_UI_EVENT = "ui event"
    }
}