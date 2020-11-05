package com.droid47.petpot.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.launcher.presentation.ui.LauncherNavigator
import javax.inject.Inject

class LauncherViewModel @Inject constructor(
    application: Application,
    val launcherNavigator: LauncherNavigator,
    private val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application) {

    fun updateFirebaseCollectionStatus() {
        firebaseManager.setCollectionEnabled()
    }

    companion object {
        const val EVENT_NAVIGATE_TO_HOME = 19080L
    }
}