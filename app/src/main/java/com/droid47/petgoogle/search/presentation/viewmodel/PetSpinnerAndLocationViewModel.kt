package com.droid47.petgoogle.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petgoogle.base.extensions.isEmpty
import com.droid47.petgoogle.base.extensions.switchMap
import com.droid47.petgoogle.base.firebase.CrashlyticsExt
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.droid47.petgoogle.search.domain.interactors.FetchPetNamesUseCase
import com.droid47.petgoogle.search.domain.interactors.RefreshFilterUseCase
import com.droid47.petgoogle.search.domain.interactors.RefreshSelectedPetUseCase
import io.reactivex.Completable
import io.reactivex.CompletableObserver
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

    val currentLocationLiveData = LiveEvent<Unit>()

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

    fun onFabClick() {
        if(locationLiveData.value.isEmpty()) {
            currentLocationLiveData.postValue(Unit)
        } else {
            locationLiveData.postValue("")
        }
    }

    companion object {
        private const val REQUEST_FETCH_NAMES = 111
        const val REQUEST_REFRESH_SELECTED_PET = 112
    }
}