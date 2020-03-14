package com.droid47.petgoogle.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petgoogle.base.firebase.Analytics
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.droid47.petgoogle.home.domain.usecases.PlayServiceAvailabilityUseCase
import javax.inject.Inject

class LauncherViewModel @Inject constructor(
    application: Application,
    private val analytics: Analytics
) : BaseAndroidViewModel(application) {

    fun updateCollectionStatus() {
        analytics.setCollectionEnabledOnTncStatus()
    }
}