package com.droid47.petpot.home.presentation.viewmodels

import android.app.Application
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.RemoteConfigUseCase
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.home.data.AppPrivacyPolicyEntity
import com.droid47.petpot.home.data.AppUpgradeEntity
import com.droid47.petpot.home.presentation.ui.HomeNavigator
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REQUEST_APP_UPGRADE_STATUS = 2330L

@ActivityScope
class HomeViewModel @Inject constructor(
    application: Application,
    val homeNavigator: HomeNavigator,
    private val remoteConfigUseCase: RemoteConfigUseCase,
    private val appSharedPreference: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    val eventLiveData = LiveEvent<Long>()
    val upgradeStatusLiveData = LiveEvent<BaseStateModel<AppUpgradeEntity>>()

    init {
        findAppUpgradeRequired()
    }

    private fun findAppUpgradeRequired() {
        remoteConfigUseCase.execute(Unit,
            object : SingleObserver<BaseStateModel<Unit>> {
                override fun onSuccess(stateModel: BaseStateModel<Unit>) {
                    when (stateModel) {
                        is Success -> {
                            processPolicyUpgradeVerification(
                                remoteConfigUseCase.getPolicyUpgradeInfoEntity()
                            )
                            processAppUpgrade(remoteConfigUseCase.getAppUpgradeInfoEntity())
                        }

                        is Failure -> onError(stateModel.error)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_APP_UPGRADE_STATUS, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                    upgradeStatusLiveData.postValue(Failure(e))
                }

            })
    }

    private fun processPolicyUpgradeVerification(appPrivacyPolicyEntity: AppPrivacyPolicyEntity?) {
        appPrivacyPolicyEntity?.run {
            if (isUpdateRequired(appSharedPreference)) {
                updatePreference(appSharedPreference)
            }
        }
    }

    private fun processAppUpgrade(appUpgradeEntity: AppUpgradeEntity?) {
        appUpgradeEntity?.run {
            if (isUpdateRequired()) {
                upgradeStatusLiveData.postValue(Success(this))
            }
        }
    }

    companion object {
        const val EVENT_TOGGLE_NAVIGATION = 71L
        const val EVENT_NAVIGATE_BACK = 72L
    }
}