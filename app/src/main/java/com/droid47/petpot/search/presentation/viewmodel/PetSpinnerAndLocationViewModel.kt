package com.droid47.petpot.search.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petpot.app.di.scopes.ActivityScope
import com.droid47.petpot.app.di.scopes.FragmentScope
import com.droid47.petpot.base.extensions.applyIOSchedulers
import com.droid47.petpot.base.extensions.logD
import com.droid47.petpot.base.extensions.switchMap
import com.droid47.petpot.base.extensions.toLiveData
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.Failure
import com.droid47.petpot.base.widgets.Success
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.search.domain.interactors.*
import com.droid47.petpot.search.presentation.viewmodel.tracking.TrackPetSpinnerAndLocationViewModel
import io.reactivex.CompletableObserver
import io.reactivex.Observer
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
    private val subscribeToLocationChangeUseCase: SubscribeToLocationChangeUseCase,
    val localPreferenceDataSource: LocalPreferencesRepository,
    private val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackPetSpinnerAndLocationViewModel {

    private val petAndLocationUpdateSubject =
        PublishSubject.create<Pair<String, String?>>().toSerialized()
    private val _petNamesLiveData = MutableLiveData<List<String>>()
    val petNamesLiveData: LiveData<List<String>>
        get() = _petNamesLiveData

    val selectedPetNameLiveData = MutableLiveData<String>()

    val locationLiveData =
        subscribeToLocationChangeUseCase.buildUseCaseObservableWithSchedulers(Unit)
            .onErrorReturn { Failure(it, "") }.toLiveData()

    val eventLiveData = LiveEvent<Long>()

    init {
        fetchPetNames()
        initInsertEvent()
    }

    override fun trackWhatPetSelected(petName: String) {
        firebaseManager.logUiEvent("Selected Pet is $petName", AnalyticsAction.CLICK)
    }

    override fun trackWhatLocationSelected(locationName: String) {
        firebaseManager.logUiEvent(
            "Selected location is $locationName",
            AnalyticsAction.ENTRY
        )
    }

    override fun trackOnCurrentLocationSelected() {
        firebaseManager.logUiEvent("Current location click", AnalyticsAction.CLICK)
    }

    fun onMiniFabClick() {
        if (locationLiveData.value?.data.isNullOrEmpty()) {
            trackOnCurrentLocationSelected()
            eventLiveData.postValue(EVENT_CURRENT_LOCATION)
        } else {
            eventLiveData.postValue(EVENT_CLEAR_LOCATION)
        }
    }

    fun onPetOrLocationSelected(petName: String, locationName: String? = null) {
        trackWhatPetSelected(petName)
        locationName?.takeIf { it.isNotEmpty() }?.run {
            trackWhatLocationSelected(this)
        }
        petAndLocationUpdateSubject.onNext(Pair(petName, locationName))
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


    @SuppressLint("CheckResult")
    private fun initInsertEvent() {
        petAndLocationUpdateSubject.debounce(400, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMapCompletable { petLocationPair ->
                refreshSelectedPetUseCase.buildUseCaseCompletableWithSchedulers(petLocationPair.first)
                    .andThen(
                        refreshFilterUseCase.buildUseCaseCompletableWithSchedulers(
                            petLocationPair.second
                        )
                    )
                    .andThen(removeAllPetsUseCase.buildUseCaseCompletableWithSchedulers(false))
            }
            .applyIOSchedulers()
            .subscribeWith(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(REQUEST_REFRESH_SELECTED_PET, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }

            })
    }

    companion object {
        const val EVENT_CURRENT_LOCATION = 113L
        const val EVENT_CLEAR_LOCATION = 114L
    }
}