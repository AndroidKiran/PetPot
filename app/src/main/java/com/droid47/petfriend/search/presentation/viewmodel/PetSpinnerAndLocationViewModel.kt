package com.droid47.petfriend.search.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.search.domain.interactors.FetchPetNamesUseCase
import com.droid47.petfriend.search.domain.interactors.RefreshFilterUseCase
import com.droid47.petfriend.search.domain.interactors.RefreshSelectedPetUseCase
import com.droid47.petfriend.search.domain.interactors.RemoveAllPetsUseCase
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val REQUEST_FETCH_NAMES = 111L
private const val REQUEST_REFRESH_SELECTED_PET = 112L

class PetSpinnerAndLocationViewModel @Inject constructor(
    application: Application,
    private val refreshFilterUseCase: RefreshFilterUseCase,
    private val refreshSelectedPetUseCase: RefreshSelectedPetUseCase,
    private val fetchPetNamesUseCase: FetchPetNamesUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase,
    val localPreferenceDataSource: LocalPreferencesRepository
) : BaseAndroidViewModel(application) {

    private val subject = PublishSubject.create<String>().toSerialized()
    private val _petNamesLiveData = MutableLiveData<List<String>>()
    val petNamesLiveData: LiveData<List<String>>
        get() = _petNamesLiveData

    val selectedPetNameLiveData = MutableLiveData<String>()
    val locationLiveData = MutableLiveData<String>()
    val eventLiveData = LiveEvent<Long>()

    init {
        fetchPetNames()
        initInsertEvent()
    }

    fun onClearLocation() {
        locationLiveData.postValue("")
    }

    fun onMiniFabClick() {
        if (locationLiveData.value.isNullOrEmpty()) {
            eventLiveData.postValue(EVENT_CURRENT_LOCATION)
        } else {
            onClearLocation()
        }
    }

    fun onPetSelected(petName: String) {
        subject.onNext(petName)
    }

    private fun fetchPetNames() =
        fetchPetNamesUseCase.execute(Unit, observer = object : SingleObserver<List<String>> {

            override fun onSuccess(petNames: List<String>) {
                _petNamesLiveData.postValue(petNames)
            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_FETCH_NAMES, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }

        })

    private fun refreshSelectedPet(petType: String) =
        refreshSelectedPetUseCase.buildUseCaseCompletable(petType)
            .andThen(refreshFilterUseCase.buildUseCaseCompletable(locationLiveData.value ?: ""))
            .andThen(removeAllPetsUseCase.buildUseCaseCompletable(false))

    @SuppressLint("CheckResult")
    private fun initInsertEvent() {
        subject.debounce(100, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMapCompletable { refreshSelectedPet(it) }
            .subscribeWith(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(
                        REQUEST_REFRESH_SELECTED_PET,
                        d
                    )
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }

            })
    }

    companion object {
        const val EVENT_CURRENT_LOCATION = 113L
    }
}