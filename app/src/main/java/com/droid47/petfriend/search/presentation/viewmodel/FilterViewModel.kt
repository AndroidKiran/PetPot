package com.droid47.petfriend.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petfriend.base.extensions.switchMap
import com.droid47.petfriend.base.extensions.toLiveData
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.BaseAndroidViewModel
import com.droid47.petfriend.base.widgets.BaseCheckableEntity
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.components.LiveEvent
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity
import com.droid47.petfriend.search.domain.interactors.*
import com.droid47.petfriend.search.presentation.ui.widgets.FilterAdapter
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
    private val updateFilterOnCloseUseCase: UpdateFilterOnCloseUseCase
) : BaseAndroidViewModel(application) {

    val categoryLiveData = MutableLiveData<String>()
    val eventLiveData = LiveEvent<Long>()

    val filterListForSelectedCategoryLiveData = categoryLiveData.switchMap { category ->
        fetchFilterItemsForSelectedCategoryUseCase.buildUseCaseObservable(category).toLiveData()
    }

    val searchFilterLiveData = MutableLiveData<String>()
    val menuItemListLiveData = MutableLiveData<List<String>>()

    val selectedFilterListLiveData = menuItemListLiveData.switchMap { menuList ->
        fetchSelectedFiltersForCategoriesUseCase.buildUseCaseObservable(menuList).toLiveData()
    }

    val appliedFilterLiveData = menuItemListLiveData.switchMap { menuList ->
        fetchAppliedFiltersForCategoriesUseCase.buildUseCaseObservable(menuList).toLiveData()
    }

    private val _sortFilterLiveData = MutableLiveData<BaseStateModel<PetFilterCheckableEntity>>()
    val sortPetFilterLiveDataEntity: LiveData<BaseStateModel<PetFilterCheckableEntity>>
        get() = _sortFilterLiveData

    init {
        closeFilter(false)
    }

    val onItemCheck = { baseCheckableEntity: BaseCheckableEntity ->
        if (baseCheckableEntity is PetFilterCheckableEntity) {
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
        val menuList = menuItemListLiveData.value ?: emptyList()
        resetFilterUseCase.buildUseCaseCompletable(menuList)
            .andThen(removeAllPetsUseCase.buildUseCaseCompletable(false))
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
        val menuList = menuItemListLiveData.value ?: emptyList()
        updateFilterOnAppliedUseCase.buildUseCaseCompletable(menuList)
            .andThen(removeAllPetsUseCase.buildUseCaseCompletable(false))
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