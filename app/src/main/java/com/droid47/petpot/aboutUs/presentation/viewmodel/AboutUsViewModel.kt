package com.droid47.petpot.aboutUs.presentation.viewmodel

import android.app.Application
import com.droid47.petpot.aboutUs.presentation.viewmodel.tracking.TrackAboutUsViewModel
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import javax.inject.Inject

class AboutUsViewModel @Inject constructor(
    application: Application,
    val fireBaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackAboutUsViewModel {

    override fun trackRateOnPlayStore() {
        fireBaseManager.logUiEvent("Rate on PlayStore", AnalyticsAction.CLICK)
    }

    override fun trackSharePlayStoreLink() {
        fireBaseManager.logUiEvent("Share App playStore Link", AnalyticsAction.CLICK)
    }

    override fun trackWriteToMail() {
        fireBaseManager.logUiEvent("Write a Mail To Team", AnalyticsAction.CLICK)
    }
}