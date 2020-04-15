package com.droid47.petfriend.home.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.home.data.UpgradeInfoEntity
import com.droid47.petfriend.home.domain.usecases.FetchAppUpgradeUseCase
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REQUEST_APP_UPGRADE_STATUS = 2330

class HomeViewModel @Inject constructor(
    application: Application,
    private val fetchAppUpgradeUseCase: FetchAppUpgradeUseCase
) : BaseAndroidViewModel(application) {

    val eventLiveData = LiveEvent<Int>()
    val upgradeStatusLiveData = LiveEvent<BaseStateModel<UpgradeInfoEntity>>()
    val similarPetList = MutableLiveData<List<PetEntity>>()

    init {
        findImmediateAppUpgradeRequired()
    }

    private fun findImmediateAppUpgradeRequired() {
        fetchAppUpgradeUseCase.execute(
            Unit,
            object : SingleObserver<BaseStateModel<UpgradeInfoEntity>> {
                override fun onSuccess(stateModel: BaseStateModel<UpgradeInfoEntity>) {
                    upgradeStatusLiveData.postValue(stateModel)
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