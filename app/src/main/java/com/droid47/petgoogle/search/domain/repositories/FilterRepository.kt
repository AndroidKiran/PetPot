package com.droid47.petgoogle.search.domain.repositories

import com.droid47.petgoogle.search.data.models.FilterItemEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FilterRepository {

    fun getAppliedFilterItemForSearch(): Single<List<FilterItemEntity>>

    fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>>

    fun getSelectedFilterItemsForCategory(type: String): Flowable<List<FilterItemEntity>>

    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>>

    fun getFilterItemForCategory(type: String): Single<FilterItemEntity>

    fun updateFilterItem(filterItemEntity: FilterItemEntity): Completable

    fun updateFilterForCategory(checked: Boolean, type: String): Completable

    fun resetFilter(categories: List<String>): Completable

    fun updateOrInsertTheFilter(filterItemEntity: FilterItemEntity): Completable

    fun updateLocationFilter(filterItemEntity: FilterItemEntity): Completable

    fun updateSortFilter(filterItemEntity: FilterItemEntity): Completable

    fun fetchPageFilterOnUpdate(type: String): Flowable<FilterItemEntity>

    fun updateLastAppliedFilter(checked: Boolean, filterNameList: List<String>): Completable

    fun refreshFilter(list: List<FilterItemEntity>): Completable

    fun getSearchLimit(): Int

}