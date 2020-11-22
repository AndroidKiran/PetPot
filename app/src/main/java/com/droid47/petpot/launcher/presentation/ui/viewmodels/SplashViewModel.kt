package com.droid47.petpot.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.*
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.launcher.domain.interactors.SyncPetTypeUseCase
import com.droid47.petpot.launcher.presentation.ui.viewmodels.tracking.TrackSplashViewModel
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val REQUEST_INIT = 101L

class SplashViewModel @Inject constructor(
    application: Application,
    private val syncPetTypeUseCase: SyncPetTypeUseCase,
    private val localPreferencesRepository: LocalPreferencesRepository,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackSplashViewModel {

    private val _resultEvent = LiveEvent<BaseStateModel<List<PetTypeEntity>>>()
    val resultEvent: LiveEvent<BaseStateModel<List<PetTypeEntity>>>
        get() = _resultEvent

    val navigationEvent = LiveEvent<String>()

    init {
        if (getTncStatus()) {
            performPetSyncCall()
        } else {
            navigationEvent.postValue(executeNavigationFlow())
        }
    }

    override fun trackSplashToOnBoarding() {
        firebaseManager.logUiEvent(AnalyticsAction.SPLASH_TO_ON_BOARDING, AnalyticsAction.AUTO)
    }

    override fun trackSplashToHome() {
        firebaseManager.logUiEvent(AnalyticsAction.SPLASH_TO_HOME, AnalyticsAction.AUTO)
    }

    override fun trackSplashToTnc() {
        firebaseManager.logUiEvent(AnalyticsAction.SPLASH_TO_TNC, AnalyticsAction.AUTO)
    }

    override fun trackRetry() {
        firebaseManager.logUiEvent("Retry on fail", AnalyticsAction.CLICK)
    }

    fun retryStartOneTimeAuthRequest() {
        trackRetry()
        performPetSyncCall()
    }

    private fun performPetSyncCall() {
        syncPetTypeUseCase.execute(true, 400, TimeUnit.MILLISECONDS, object :
            SingleObserver<BaseStateModel<List<PetTypeEntity>>> {
            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_INIT, d)
                _resultEvent.postValue(Loading())
            }

            override fun onSuccess(baseStateModel: BaseStateModel<List<PetTypeEntity>>) {
                _resultEvent.postValue(baseStateModel)
                if (baseStateModel is Success) {
                    navigationEvent.postValue(executeNavigationFlow())
                }
            }

            override fun onError(e: Throwable) {
                _resultEvent.postValue(Failure(e, null))
                CrashlyticsExt.handleException(e)
            }
        })
    }

    fun getTncStatus() = localPreferencesRepository.getTnCState()

    private fun executeNavigationFlow(): String = when {
        !localPreferencesRepository.getOnBoardingState() -> TO_INTRO
        !localPreferencesRepository.getTnCState() -> TO_TNC
        else -> TO_HOME
    }

    companion object {
        const val TO_INTRO = "to_intro"
        const val TO_TNC = "to_tnc"
        const val TO_HOME = "to_home"
    }
}