package com.droid47.petpot.search.presentation.viewmodel

import android.app.Application
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.search.presentation.viewmodel.tracking.TrackSettingsViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    application: Application,
    val fireBaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackSettingsViewModel {

    override fun trackThemeChange(theme: String) {
        fireBaseManager.logUiEvent("Selected Theme : $theme", AnalyticsAction.CLICK)
    }

    override fun trackNotificationState(state: Boolean) {
        fireBaseManager.logUiEvent("Notification State : $state", AnalyticsAction.CLICK)
    }
}