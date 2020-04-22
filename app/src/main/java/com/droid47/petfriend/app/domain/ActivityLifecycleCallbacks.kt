package com.droid47.petfriend.app.domain

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.workmanagers.SyncPetTypeWorker
import com.droid47.petfriend.workmanagers.TriggerLocalNotificationWorker
import javax.inject.Inject

class ActivityLifecycleCallbacks @Inject constructor(private val application: Application) :
    Application.ActivityLifecycleCallbacks {

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
            TriggerLocalNotificationWorker.enqueuePeriodicRequest(application)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}