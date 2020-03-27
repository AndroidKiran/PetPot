package com.droid47.petgoogle.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid47.petgoogle.base.extensions.switchMap
import com.droid47.petgoogle.base.extensions.toLiveData
import com.droid47.petgoogle.base.firebase.CrashlyticsExt
import com.droid47.petgoogle.base.widgets.BaseAndroidViewModel
import com.droid47.petgoogle.base.widgets.BaseStateModel
import com.droid47.petgoogle.base.widgets.Failure
import com.droid47.petgoogle.base.widgets.components.LiveEvent
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.data.models.PAGE_NUM
import com.droid47.petgoogle.search.domain.interactors.*
import com.droid47.petgoogle.search.presentation.widgets.FilterAdapter
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val REFRESH_SELECTED_FILTER = 3000
private const val CLEAR_SELECTED_FILTER_BY_CATEGORY = 3001
private const val CLEAR_ALL_SELECTED_FILTER = 3002
private const val APPLY_FILTER = 3003
private const val APPLY_LAST_FILTER = 3004
private const val FETCH_SORT_FILTER = 3005
private const val APPLY_SORT_FILTER = 3006

class FilterViewModel @Inject constructor(
    application: Application,
    private val fetchFilterItemsForSelectedCategoryUseCase: FetchFilterItemsForSelectedCategoryUseCase,
    private val refreshSelectedFilterItemUseCase: RefreshSelectedFilterItemUseCase,
    private val fetchSelectedFiltersForCategoryUseCase: FetchSelectedFiltersForCategoryUseCase,
    private val fetchSelectedFiltersForCategoriesUseCase: FetchSelectedFiltersForCategoriesUseCase,
    private val resetFilterForCategoryUseCase: ResetFilterForCategoryUseCase,
    private val resetFilterUseCase: ResetFilterUseCase,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val updateLastAppliedFilterUseCase: UpdateLastAppliedFilterUseCase,
    private val fetchSortMenuStateUseCase: FetchSortMenuStateUseCase,
    private val refreshSortMenuUseCase: RefreshSortMenuUseCase
) : BaseAndroidViewModel(application), FilterAdapter.OnItemCheckListener {

    private val _lastAppliedFilterItemList =
        MutableLiveData<BaseStateModel<List<FilterItemEntity>>>()

    val categoryLiveData = MutableLiveData<String>()

    val filterListForSelectedCategoryLiveData = categoryLiveData.switchMap { category ->
        fetchFilterItemsForSelectedCategoryUseCase.buildUseCaseObservable(category).toLiveData()
    }

    val isFilterAppliedForCategoryLiveData = categoryLiveData.switchMap { category ->
        fetchSelectedFiltersForCategoryUseCase.buildUseCaseObservable(category).toLiveData()
    }

    val searchFilterLiveData = MutableLiveData<String>()

    val menuItemListLiveData = MutableLiveData<List<String>>()

    val allSelectedFilterListLiveData = menuItemListLiveData.switchMap { menuList ->
        fetchSelectedFiltersForCategoriesUseCase.buildUseCaseObservable(menuList).toLiveData()
    }

    val eventLiveData = LiveEvent<Int>()

    private val _sortFilterLiveData = MutableLiveData<BaseStateModel<FilterItemEntity>>()
    val sortFilterLiveDataEntity: LiveData<BaseStateModel<FilterItemEntity>>
        get() = _sortFilterLiveData

    override fun onItemCheck(filterItemEntity: FilterItemEntity) {
        refreshSelectedFilterItemUseCase.execute(filterItemEntity, object : CompletableObserver {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(REFRESH_SELECTED_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }
        })
    }

    fun resetFilterForCategory(category: String) {
        resetFilterForCategoryUseCase.execute(category, object : CompletableObserver {

            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(CLEAR_SELECTED_FILTER_BY_CATEGORY, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }
        })
    }

    fun resetFilter(menuList: List<String>) {
        resetFilterUseCase.execute(menuList, object : CompletableObserver {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(CLEAR_ALL_SELECTED_FILTER, d)
            }

            override fun onError(e: Throwable) {
                CrashlyticsExt.handleException(e)
            }
        })
    }

    fun applyFilter() {
        applyFilterUseCase.execute(
            FilterItemEntity(1.toString(), PAGE_NUM, true),
            object : CompletableObserver {

                override fun onComplete() {
                    _lastAppliedFilterItemList.value = allSelectedFilterListLiveData.value
                    eventLiveData.postValue(EVENT_APPLY_FILTER)
                }

                override fun onSubscribe(d: Disposable) {
                    registerRequest(APPLY_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    eventLiveData.postValue(EVENT_APPLY_FILTER)
                    CrashlyticsExt.handleException(e)
                }
            })
    }

    fun onFilterActive() =
        updateLastAppliedFilterUseCase.execute(_lastAppliedFilterItemList.value?.data
            ?: emptyList(),
            object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    registerRequest(APPLY_LAST_FILTER, d)
                }

                override fun onError(e: Throwable) {
                    CrashlyticsExt.handleException(e)
                }
            })

    fun fetchSortMenuState() =
        fetchSortMenuStateUseCase.execute(observer = object :
            SingleObserver<BaseStateModel<FilterItemEntity>> {

            override fun onSuccess(baseStateModel: BaseStateModel<FilterItemEntity>) {
                _sortFilterLiveData.postValue(baseStateModel)
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(FETCH_SORT_FILTER, d)
            }

            override fun onError(e: Throwable) {
                _sortFilterLiveData.postValue(Failure(e))
            }
        })

    fun refreshSortFilter(filterItemEntity: FilterItemEntity) =
        refreshSortMenuUseCase.execute(filterItemEntity, object : CompletableObserver {

            override fun onComplete() {
                eventLiveData.postValue(EVENT_APPLY_SORT_FILTER)
            }

            override fun onSubscribe(d: Disposable) {
                registerRequest(APPLY_SORT_FILTER, d)
            }

            override fun onError(e: Throwable) {
                eventLiveData.postValue(EVENT_APPLY_SORT_FILTER)
                CrashlyticsExt.handleException(e)
            }

        })

    fun closeFilter() {
        eventLiveData.postValue(EVENT_CLOSE_FILTER)
    }

    companion object {
        const val EVENT_APPLY_FILTER = 111
        const val EVENT_APPLY_SORT_FILTER = 112
        const val EVENT_CLOSE_FILTER = 113
    }

}