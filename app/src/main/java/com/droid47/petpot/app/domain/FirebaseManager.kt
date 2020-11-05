package com.droid47.petpot.app.domain

import android.app.Activity
import android.app.Application
import com.droid47.petpot.BuildConfig
import com.droid47.petpot.R
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject

class FirebaseManager @Inject constructor(
    application: Application,
    private val localPreferencesRepository: LocalPreferencesRepository
) : IFirebaseManager {

    private val firebaseFirebaseAnalytics: FirebaseAnalytics =
        FirebaseAnalytics.getInstance(application.applicationContext)
    private val firebasePerformance = FirebasePerformance.getInstance()
    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    private val firebaseMessaging = FirebaseMessaging.getInstance()
    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun setCollectionEnabled() {
        val tncStatus = localPreferencesRepository.getTnCState()
        setCollectionEnabled(tncStatus)
        setUserId(tncStatus)
    }

    override fun setCollectionDisabled() {
        setCollectionEnabled(false)
    }

    override fun sendScreenView(screenName: String, className: String, activity: Activity) {
        firebaseFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_SCREENVIEW)
        }
    }

    override fun logUiEvent(itemId: String, action: String) {
        firebaseFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, itemId)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_UI_EVENT)
            param(FA_KEY_UI_ACTION, action)
        }
    }

    private fun setCollectionEnabled(status: Boolean) {
        firebaseFirebaseAnalytics.setAnalyticsCollectionEnabled(status)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(status)
        firebasePerformance.isPerformanceCollectionEnabled = status
        firebaseMessaging.isAutoInitEnabled = status
        firebaseRemoteConfig.let {
            it.setDefaultsAsync(R.xml.remote_config_defaults)
            it.setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds =
                        if (BuildConfig.DEBUG) 0 else ((60 * 8).toLong())
                }
            )
        }
    }

    private fun setUserId(tncStatus: Boolean) {
        if (!tncStatus) return
        val instanceId = localPreferencesRepository.getFcmToken() ?: return
        firebaseFirebaseAnalytics.setUserId(instanceId)
        firebaseCrashlytics.setUserId(instanceId)
    }

    companion object {
        private const val FA_CONTENT_TYPE_SCREENVIEW = "screen"
        private const val FA_KEY_UI_ACTION = "ui_action"
        private const val FA_CONTENT_TYPE_UI_EVENT = "ui event"
    }
}