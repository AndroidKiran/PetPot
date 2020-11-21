package com.droid47.petpot.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petpot.base.extensions.applyIOSchedulers
import com.droid47.petpot.base.extensions.switchMap
import com.droid47.petpot.base.extensions.toLiveData
import com.droid47.petpot.base.firebase.AnalyticsAction
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseAndroidViewModel
import com.droid47.petpot.base.widgets.BaseCheckableEntity
import com.droid47.petpot.base.widgets.BaseStateModel
import com.droid47.petpot.base.widgets.components.LiveEvent
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.domain.interactors.*
import com.droid47.petpot.search.presentation.viewmodel.tracking.TrackFilterViewModel
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REFRESH_SELECTED_FILTER = 3000L
private const val CLEAR_ALL_SELECTED_FILTER = 3002L
private const val APPLY_FILTER = 3003L

class FilterViewModel @Inject constructor(
    application: Application,
    private val fetchFilterItemsForSelectedCategoryUseCase: FetchFilterItemsForSelectedCategoryUseCase,
    private val refreshSelectedFilterItemUseCase: RefreshSelectedFilterItemUseCase,
    private val fetchSelectedFiltersForCategoriesUseCase: FetchSelectedFiltersForCategoriesUseCase,
    private val resetFilterUseCase: ResetFilterUseCase,
    private val fetchAppliedFiltersForCategoriesUseCase: FetchAppliedFiltersForCategoriesUseCase,
    private val updateFilterOnAppliedUseCase: UpdateFilterOnAppliedUseCase,
    private val removeAllPetsUseCase: RemoveAllPetsUseCase,
    private val updateFilterOnCloseUseCase: UpdateFilterOnCloseUseCase,
    private val firebaseManager: IFirebaseManager
) : BaseAndroidViewModel(application), TrackFilterViewModel {

    val categoryLiveData = MutableLiveData<String>()
    val eventLiveData = LiveEvent<Long>()

    val filterListForSelectedCategoryLiveData = categoryLiveData.switchMap { category ->
        fetchFilterItemsForSelectedCategoryUseCase.buildUseCaseObservableWithSchedulers(category)
            .toLiveData()
    }

    val searchFilterLiveData = MutableLiveData<String>()
    val menuItemListLiveData = MutableLiveData<List<String>>()

    val selectedFilterListLiveData = menuItemListLiveData.switchMap { menuList ->
        fetchSelectedFiltersForCategoriesUseCase.buildUseCaseObservableWithSchedulers(menuList)
            .toLiveData()
    }

    val appliedFilterLiveData = menuItemListLiveData.switchMap { menuList ->
        fetchAppliedFiltersForCategoriesUseCase.buildUseCaseObservableWithSchedulers(menuList)
            .toLiveData()
    }

    private val _sortFilterLiveData = MutableLiveData<BaseStateModel<PetFilterCheckableEntity>>()
    val sortPetFilterLiveDataEntity: LiveData<BaseStateModel<PetFilterCheckableEntity>>
        get() = _sortFilterLiveData

    init {
        closeFilter(false)
    }

    override fun trackFilterChange(category: String) {
        firebaseManager.logUiEvent("Filter Category : $category", AnalyticsAction.CLICK)
    }

    override fun trackFilterItemSelected(selected: Boolean) {
        firebaseManager.logUiEvent("Filter item selected : $selected", AnalyticsAction.CLICK)
    }

    override fun trackFilterApplied() {
        firebaseManager.logUiEvent("Filter Applied", AnalyticsAction.CLICK)
    }

    override fun trackFilterReset() {
        firebaseManager.logUiEvent("Filter Reset", AnalyticsAction.CLICK)
    }

    override fun trackFilterClose() {
        firebaseManager.logUiEvent("Filter Close", AnalyticsAction.CLICK)
    }

    val onItemCheck = { baseCheckableEntity: BaseCheckableEntity ->
        if (baseCheckableEntity is PetFilterCheckableEntity) {
            trackFilterItemSelected(baseCheckableEntity.selected)
            refreshSelectedFilterItemUseCase.execute(
                baseCheckableEntity,
                object : CompletableObserver {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        registerDisposableRequest(REFRESH_SELECTED_FILTER, d)
                    }

                    override fun onError(e: Throwable) {
                        CrashlyticsExt.handleException(e)
                    }
                })
        }
    }

    fun resetFilter() {
        trackFilterReset()
        val menuList = menuItemListLiveData.value ?: emptyList()
        resetFilterUseCase.buildUseCaseCompletableWithSchedulers(menuList)
            .andThen(removeAllPetsUseCase.buildUseCaseCompletableWithSchedulers(false))
            .applyIOSchedulers()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(CLEAR_ALL_SELECTED_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    fun applyFilter() {
        trackFilterApplied()
        val menuList = menuItemListLiveData.value ?: emptyList()
        updateFilterOnAppliedUseCase.buildUseCaseCompletableWithSchedulers(menuList)
            .andThen(removeAllPetsUseCase.buildUseCaseCompletableWithSchedulers(false))
            .applyIOSchedulers()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    eventLiveData.postValue(EVENT_APPLY_FILTER)
                }

                override fun onSubscribe(d: Disposable) {
                    registerDisposableRequest(APPLY_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    eventLiveData.postValue(EVENT_APPLY_FILTER)
                    CrashlyticsExt.handleException(e)
                }
            })
    }


    fun closeFilter(notify: Boolean) {
        trackFilterClose()
        val menuList = menuItemListLiveData.value ?: emptyList()
        updateFilterOnCloseUseCase.execute(menuList, object : CompletableObserver {
            override fun onComplete() {
                if (!notify) return
                eventLiveData.postValue(EVENT_CLOSE_FILTER)
            }

            override fun onSubscribe(d: Disposable) {
                registerDisposableRequest(EVENT_CLOSE_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
                if (!notify) return
                eventLiveData.postValue(EVENT_CLOSE_FILTER)
            }
        })
    }

    fun clearSearch() {
        searchFilterLiveData.postValue("")
    }

    companion object {
        const val EVENT_APPLY_FILTER = 111L
        const val EVENT_APPLY_SORT_FILTER = 112L
        const val EVENT_CLOSE_FILTER = 113L
    }
}