package com.droid47.petfriend.app.domain

import android.app.Application
import com.droid47.petfriend.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petfriend.base.firebase.IFirebaseManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
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
    }

    private fun setUserId(tncStatus: Boolean) {
        if (!tncStatus) return
        val instanceId = localPreferencesRepository.getFcmToken() ?: return
        firebaseFirebaseAnalytics.setUserId(instanceId)
        firebaseCrashlytics.setUserId(instanceId)
    }
}