package com.droid47.petpot.app.domain

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.home.presentation.ui.HomeActivity
import com.droid47.petpot.workmanagers.SyncPetTypeWorker
import com.droid47.petpot.workmanagers.TriggerLocalNotificationWorker
import javax.inject.Inject

class PetAppActivityLifecycleCallbacks @Inject constructor(
    private val application: Application,
    private val localPreferencesRepository: LocalPreferencesRepository
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        if (HomeActivity.TAG == activity::class.java.simpleName) {
            SyncPetTypeWorker.enqueuePeriodicRequest(application)
            val notificationStatus = localPreferencesRepository.getNotificationStatus()
            TriggerLocalNotificationWorker.isNotificationEnabled(notificationStatus, application)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}