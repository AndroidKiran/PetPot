package com.droid47.petfriend.launcher.presentation.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import com.droid47.petfriend.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.*
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.launcher.domain.interactors.RefreshAuthTokenAndPetTypeUseCase
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val ONE_TIME_AUTH_TOKEN_REQUEST = 101

class SplashViewModel @Inject constructor(
    application: Application,
    private val refreshAuthTokenAndPetTypeUseCase: RefreshAuthTokenAndPetTypeUseCase,
    val localPreferencesRepository: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    private val _resultEvent = LiveEvent<BaseStateModel<List<PetTypeEntity>>>()
    val resultEvent: LiveEvent<BaseStateModel<List<PetTypeEntity>>>
        get() = _resultEvent

    val navigationEvent = LiveEvent<String>()

    init {
        startOneTimeAuthRequest()
//        SyncAnimalTypesJob(WorkManager.getInstance(application)).enqueueRequest()
    }

    @SuppressLint("CheckResult")
    fun startOneTimeAuthRequest() {
        refreshAuthTokenAndPetTypeUseCase.execute(observer = object :
            SingleObserver<BaseStateModel<List<PetTypeEntity>>> {
            override fun onSuccess(baseStateModel: BaseStateModel<List<PetTypeEntity>>) {
                _resultEvent.postValue(baseStateModel)
                if (baseStateModel is Success) {
                    navigationEvent.postValue(executeNavigationFlow())
                }
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(ONE_TIME_AUTH_TOKEN_REQUEST, d)
                _resultEvent.postValue(Loading())
            }

            override fun onError(e: Throwable) {
                _resultEvent.postValue(Failure(e, null))
                CrashlyticsExt.handleException(e)
            }

        })
    }

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