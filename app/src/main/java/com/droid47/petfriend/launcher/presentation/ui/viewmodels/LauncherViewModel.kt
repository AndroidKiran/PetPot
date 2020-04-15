package com.droid47.petfriend.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petfriend.base.firebase.Analytics
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import javax.inject.Inject

class LauncherViewModel @Inject constructor(
    application: Application,
    private val analytics: Analytics
) : BaseAndroidViewModel(application) {

    fun updateCollectionStatus() {
        analytics.setCollectionEnabledOnTncStatus()
    }
}