package com.droid47.petfriend.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.search.domain.interactors.FetchPetNamesUseCase
import com.droid47.petfriend.search.domain.interactors.RefreshFilterUseCase
import com.droid47.petfriend.search.domain.interactors.RefreshSelectedPetUseCase
import io.reactivex.Completable
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class PetSpinnerAndLocationViewModel @Inject constructor(
    application: Application,
    private val refreshFilterUseCase: RefreshFilterUseCase,
    private val refreshSelectedPetUseCase: RefreshSelectedPetUseCase,
    private val fetchPetNamesUseCase: FetchPetNamesUseCase
) : BaseAndroidViewModel(application) {

    private val _petNamesLiveData = MutableLiveData<List<String>>()
    val petNamesLiveData: LiveData<List<String>>
        get() = _petNamesLiveData

    val selectedPetNameLiveData = MutableLiveData<String>()
    val locationLiveData = LiveEvent<String>()
    val eventLiveData = LiveEvent<Int>()

    init {
        fetchPetNames()
    }

    private fun fetchPetNames() =
        fetchPetNamesUseCase.execute(observer = object : SingleObserver<List<String>> {

            override fun onSuccess(petNames: List<String>) {
                _petNamesLiveData.postValue(petNames)
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(REQUEST_FETCH_NAMES, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }

        })

    fun refreshSelectedPet(petType: String): Completable =
        refreshSelectedPetUseCase.buildUseCaseCompletable(petType)
            .andThen(refreshFilterUseCase.buildUseCaseCompletable(locationLiveData.value ?: ""))

    fun onClearLocation() {
        if (!locationLiveData.value.isNullOrEmpty()) {
            locationLiveData.postValue("")
        }
    }

    fun onMiniFabClick() {
        if(locationLiveData.value.isNullOrEmpty()) {
            eventLiveData.postValue(EVENT_CURRENT_LOCATION)
        } else {
            onClearLocation()
        }
    }


    companion object {
        private const val REQUEST_FETCH_NAMES = 111
        const val REQUEST_REFRESH_SELECTED_PET = 112
        const val EVENT_CURRENT_LOCATION = 113
    }
}