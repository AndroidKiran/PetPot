package com.droid47.petpot.launcher.presentation.ui.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.launcher.presentation.ui.models.WebViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.tracking.TrackTncViewModel
import javax.inject.Inject

class TnCViewModel @Inject constructor(
    application: Application,
    val localPreferencesRepository: LocalPreferencesRepository,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackTncViewModel {

    val webViewModel = WebViewModel()
    val acceptStateLiveData = MutableLiveData(false)

    override fun trackConsentScroll(scrollDirection: String) {
        firebaseManager.logUiEvent(
            "Consent scrolling $scrollDirection",
            AnalyticsAction.SCROLL
        )
    }

    override fun trackAcceptConsent() {
        firebaseManager.logUiEvent("Consent accepted", AnalyticsAction.CLICK)
    }

    override fun trackTncToHome() {
        firebaseManager.logUiEvent(AnalyticsAction.TNC_TO_HOME, AnalyticsAction.CLICK)
    }

    override fun trackAcceptBtnState(state: String) {
        firebaseManager.logUiEvent("Accept Button is $state", AnalyticsAction.SCROLL)
    }

    fun updateTnCStatus() {
        trackAcceptConsent()
        localPreferencesRepository.saveTnCState(true)
    }


}