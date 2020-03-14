package com.droid47.petgoogle.base.firebase

import android.app.Application
import android.content.Context
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

class FirebaseAnalytics @Inject constructor(
    private val application: Application,
    private val localPreferencesRepository: LocalPreferencesRepository
) : Analytics {

    private val firebaseFirebaseAnalytics: FirebaseAnalytics =
        FirebaseAnalytics.getInstance(application.applicationContext)

    override fun setCollectionEnabled(status: Boolean) {
        firebaseFirebaseAnalytics.setAnalyticsCollectionEnabled(status)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = status
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(status)
        FirebaseMessaging.getInstance().isAutoInitEnabled = status
    }

    override fun setCollectionEnabledOnTncStatus() {
        val tncStatus = localPreferencesRepository.getTnCState()
        setCollectionEnabled(tncStatus)
        setUserId(tncStatus)
    }

    private fun setUserId(tncStatus: Boolean) {
        if (!tncStatus) return
        val instanceId = localPreferencesRepository.getFcmToken() ?: return
        firebaseFirebaseAnalytics.setUserId(instanceId)
        FirebaseCrashlytics.getInstance().setUserId(instanceId)
    }

    override fun getContext(): Context = application.applicationContext
}