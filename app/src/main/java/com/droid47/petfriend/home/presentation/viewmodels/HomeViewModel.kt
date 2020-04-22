package com.droid47.petfriend.home.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.firebase.RemoteConfigUseCase
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.home.data.AppUpgradeEntity
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REQUEST_APP_UPGRADE_STATUS = 2330

class HomeViewModel @Inject constructor(
    application: Application,
    private val remoteConfigUseCase: RemoteConfigUseCase
) : BaseAndroidViewModel(application) {

    val eventLiveData = LiveEvent<Int>()
    val upgradeStatusLiveData = LiveEvent<BaseStateModel<AppUpgradeEntity>>()
    val similarPetList = MutableLiveData<List<PetEntity>>()

    init {
        findAppUpgradeRequired()
    }

    private fun findAppUpgradeRequired() {
        remoteConfigUseCase.execute(
            RemoteConfigUseCase.KEY_APP_UPGRADE,
            object : SingleObserver<BaseStateModel<String>> {
                override fun onSuccess(stateModel: BaseStateModel<String>) {
                    val appUpgradeEntity =
                        remoteConfigUseCase.getUpgradeInfoEntity(stateModel.data ?: "")
                    when (appUpgradeEntity) {
                        null -> onError(IllegalStateException("AppUpgradeEntity is Null"))
                        else -> upgradeStatusLiveData.postValue(Success(appUpgradeEntity))
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    registerRequest(REQUEST_APP_UPGRADE_STATUS, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                    upgradeStatusLiveData.postValue(Failure(e))
                }

            })
    }

    companion object {
        const val EVENT_TOGGLE_NAVIGATION = 71
        const val EVENT_NAVIGATE_BACK = 72
    }
}