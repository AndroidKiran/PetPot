package com.droid47.petpot.launcher.presentation.ui.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.launcher.presentation.ui.viewmodels.tracking.TrackHomeBoardViewModel
import javax.inject.Inject

class HomeBoardViewModel @Inject constructor(
    application: Application,
    private val localPreferencesRepository: LocalPreferencesRepository,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackHomeBoardViewModel {

    val positionLiveData = MutableLiveData<Int>()

    fun saveOnBoardingState() {
        localPreferencesRepository.saveOnBoardingState()
    }

    override fun trackPagerPosition(position: Int) {
        firebaseManager.logUiEvent(
            "OnBoarding screen swipe, screen position is $position",
            AnalyticsAction.SWIPE
        )
    }

    override fun trackGetStartedAction() {
        firebaseManager.logUiEvent(
            AnalyticsAction.ON_BOARDING_TO_TNC_TRANSITION,
            AnalyticsAction.CLICK
        )
    }

    companion object {
        const val START_POSITION = 0
        const val END_POSITION = 2
    }
}