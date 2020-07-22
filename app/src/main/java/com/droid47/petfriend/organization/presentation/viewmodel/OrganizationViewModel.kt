package com.droid47.petfriend.organization.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.extensions.combineWith
import com.droid47.petfriend.base.extensions.switchMap
import com.droid47.petfriend.base.extensions.toLiveData
import com.droid47.petfriend.base.extensions.toSingleLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseCheckableEntity
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.data.models.State
import com.droid47.petfriend.organization.domain.*
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val REQUEST_CLEAR_FILTER = 998L
private const val REQUEST_APPLY_FILTER = 999L
private const val REQUEST_CLOSE_FILTER = 1000L
private const val REQUEST_UPDATE_SELECTION = 1001L

class OrganizationViewModel @Inject constructor(
    application: Application,
    private val organizationPaginationUseCase: OrganisationPaginationUseCase,
    fetchStatesUseCase: FetchStatesUseCase,
    fetchSelectedOrganizationUseCase: FetchSelectedOrganizationUseCase,
    private val clearFilterUseCase: ClearFilterUseCase,
    private val updateFilterOnAppliedUseCase: UpdateFilterOnAppliedUseCase,
    private val updateFilterOnCloseUseCase: UpdateFilterOnCloseUseCase,
    private val updateOrganizationSelectionUseCase: UpdateOrganizationSelectionUseCase
) : BaseAndroidViewModel(application) {

    val itemPaginationStateLiveData = organizationPaginationUseCase.itemPaginationStateLiveData
    val eventLiveData = LiveEvent<Long>()
    val selectedOrganizationsLiveData =
        fetchSelectedOrganizationUseCase.buildUseCaseObservable(true).toLiveData()

    val statesLiveData: LiveData<List<State>> =
        fetchStatesUseCase.buildUseCaseObservable(Unit).distinctUntilChanged().toSingleLiveData()
    val selectedPositionLiveData = LiveEvent<Int>()
    private val stateAbbreviationLiveData: LiveData<String?> =
        selectedPositionLiveData.switchMap { position ->
            when (position) {
                null -> MutableLiveData(statesLiveData.value?.get(0)?.abbreviation)
                else -> MutableLiveData(statesLiveData.value?.get(position)?.abbreviation)
            }
        }
    val organizationNameLiveData = MutableLiveData<String>()
    val inputQueryLiveData: LiveData<Pair<String?, String?>> =
        stateAbbreviationLiveData.combineWith(organizationNameLiveData) { stateAbbreviation, organizationName ->
            return@combineWith Pair(stateAbbreviation, organizationName)
        }

    val organizationsLiveData = inputQueryLiveData.switchMap { pair ->
        return@switchMap organizationPaginationUseCase.buildPageListObservable(
            pair.first,
            pair.second
        ).distinctUntilChanged()
            .debounce(400, TimeUnit.MILLISECONDS)
            .toSingleLiveData()
    }

    fun updatePage(page: Int) {
        organizationPaginationUseCase.updatePage(page)
    }

    fun clearFilter() {
        clearFilterUseCase.execute(Unit, object : CompletableObserver {
            override fun onComplete() {
                eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_CLEAR_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }

        })
    }

    fun applyFilter() {
        updateFilterOnAppliedUseCase.execute(Unit, object : CompletableObserver {
            override fun onComplete() {
                eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_APPLY_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }

        })
    }

    fun closeFilter() {
        updateFilterOnCloseUseCase.execute(Unit, object : CompletableObserver {
            override fun onComplete() {
                eventLiveData.postValue(HomeViewModel.EVENT_NAVIGATE_BACK)
            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(REQUEST_CLOSE_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }

        })
    }

    val onItemSelected = { baseCheckableEntity: BaseCheckableEntity ->
        if (baseCheckableEntity is OrganizationCheckableEntity) {
            updateOrganizationSelectionUseCase.execute(
                baseCheckableEntity,
                object : CompletableObserver {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        registerDisposableRequest(REQUEST_UPDATE_SELECTION, d)
                    }

                    override fun onError(e: Throwable) {
                        CrashlyticsExt.handleException(e)
                    }

                })
        }
    }

    companion object {
        const val EVENT_MAP_READY = 101L
    }
}