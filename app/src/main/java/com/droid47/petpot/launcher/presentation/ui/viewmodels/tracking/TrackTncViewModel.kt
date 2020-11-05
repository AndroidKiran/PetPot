package com.droid47.petpot.launcher.presentation.ui.viewmodels.tracking

interface TrackTncViewModel {
    fun trackConsentScroll(scrollDirection : String)
    fun trackAcceptConsent()
    fun trackTncToHome()
    fun trackAcceptBtnState(state: String)
}