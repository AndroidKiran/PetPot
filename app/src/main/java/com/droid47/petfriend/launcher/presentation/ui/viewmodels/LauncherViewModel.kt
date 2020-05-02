package com.droid47.petfriend.launcher.presentation.ui.viewmodels

import android.app.Application
import androidx.navigation.NavDirections
import com.droid47.petfriend.base.firebase.IFirebaseManager
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import javax.inject.Inject

class LauncherViewModel @Inject constructor(
    application: Application,
    private val IFirebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application) {

    val homeNavigationLiveData = LiveEvent<NavDirections>()

    fun updateFirebaseCollectionStatus() {
        IFirebaseManager.setCollectionEnabledOnTncStatus()
    }

    companion object {
        const val EVENT_NAVIGATE_TO_HOME = 19080L
    }
}