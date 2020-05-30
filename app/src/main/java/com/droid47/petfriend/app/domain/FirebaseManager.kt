package com.droid47.petfriend.app.domain

import android.app.Application
import com.droid47.petfriend.BuildConfig
import com.droid47.petfriend.R
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.firebase.IFirebaseManager
import com.google.firebase.analytics.FirebaseAnalytics
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

    override fun setCollectionEnabledOnTncStatus() {
        val tncStatus = localPreferencesRepository.getTnCState()
        setCollectionEnabled(tncStatus)
        setUserId(tncStatus)
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
                    minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else ((60 * 8).toLong())
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
}