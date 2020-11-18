package com.droid47.petpot.launcher.presentation.ui.viewmodels

import android.app.Application
import com.droid47.petpot.base.extensions.applyIOSchedulers
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.firebase.RemoteConfigUseCase
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.*
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.launcher.domain.interactors.SyncPetTypeUseCase
import com.droid47.petpot.launcher.presentation.ui.viewmodels.tracking.TrackSplashViewModel
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REQUEST_INIT = 101L

class SplashViewModel @Inject constructor(
    application: Application,
    private val syncPetTypeUseCase: SyncPetTypeUseCase,
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val remoteConfigUseCase: RemoteConfigUseCase,
    val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackSplashViewModel {

    private val _resultEvent = LiveEvent<BaseStateModel<List<PetTypeEntity>>>()
    val resultEvent: LiveEvent<BaseStateModel<List<PetTypeEntity>>>
        get() = _resultEvent

    val navigationEvent = LiveEvent<String>()

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
        bindPetSyncAndPolicyStatusAsync()
    }


    private fun getPetTypeSyncData(): Single<BaseStateModel<List<PetTypeEntity>>> =
        syncPetTypeUseCase.buildUseCaseSingleWithSchedulers(true)

    private fun getPolicyUpgradeStatus(): Single<BaseStateModel<String>> =
        remoteConfigUseCase.buildUseCaseSingleWithSchedulers(RemoteConfigUseCase.KEY_PRIVACY_POLICY_UPGRADE)

    fun bindPetSyncAndPolicyStatusAsync() {
        Single.zip(
            getPetTypeSyncData(),
            getPolicyUpgradeStatus(),
            { petTypeEntityStateModel: BaseStateModel<List<PetTypeEntity>>, policyConfigStateModel: BaseStateModel<String> ->
                Pair(petTypeEntityStateModel, policyConfigStateModel)
            }).applyIOSchedulers()
            .subscribe(object :
                SingleObserver<Pair<BaseStateModel<List<PetTypeEntity>>, BaseStateModel<String>>> {
                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_INIT, d)
                    _resultEvent.postValue(Loading())
                }

                override fun onSuccess(resultPair: Pair<BaseStateModel<List<PetTypeEntity>>, BaseStateModel<String>>) {
                    processPolicyUpgradeVerification(resultPair.second)
                    _resultEvent.postValue(resultPair.first)
                    if (resultPair.first is Success) {
                        navigationEvent.postValue(executeNavigationFlow())
                    }
                }

                override fun onError(e: Throwable) {
                    _resultEvent.postValue(Failure(e, null))
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    private fun processPolicyUpgradeVerification(policyConfigStateModel: BaseStateModel<String>) {
        val privacyPolicyUpgradeEntity =
            remoteConfigUseCase.getPolicyUpgradeInfoEntity(policyConfigStateModel.data ?: "")
                ?: return
        if (privacyPolicyUpgradeEntity.isUpdateRequired(localPreferencesRepository)) {
            privacyPolicyUpgradeEntity.updatePreference(localPreferencesRepository)
        }
    }


//    @SuppressLint("CheckResult")
//    fun startOneTimeAuthRequest() {
//        syncPetTypeUseCase.execute(true, 200, TimeUnit.MILLISECONDS, observer = object :
//            SingleObserver<BaseStateModel<List<PetTypeEntity>>> {
//            override fun onSuccess(baseStateModel: BaseStateModel<List<PetTypeEntity>>) {
//                _resultEvent.postValue(baseStateModel)
//                if (baseStateModel is Success) {
//                    navigationEvent.postValue(executeNavigationFlow())
//                }
//            }
//
//            override fun onSubscribe(d: Disposable) {
//                registerDisposableRequest(ONE_TIME_AUTH_TOKEN_REQUEST, d)
//                _resultEvent.postValue(Loading())
//            }
//
//            override fun onError(e: Throwable) {
//                _resultEvent.postValue(Failure(e, null))
//                CrashlyticsExt.handleException(e)
//            }
//        })
//    }
//
//    private fun findPolicyUpgradeRequired() {
//        remoteConfigUseCase.execute(
//            RemoteConfigUseCase.KEY_PRIVACY_POLICY_UPGRADE,
//            object : SingleObserver<BaseStateModel<String>> {
//                override fun onSuccess(stateModel: BaseStateModel<String>) {
//
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                    registerDisposableRequest(REQUEST_POLICY_UPGRADE, d)
//                }
//
//                override fun onError(e: Throwable) {
//                    CrashlyticsExt.handleException(e)
//                }
//
//            })
//    }

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