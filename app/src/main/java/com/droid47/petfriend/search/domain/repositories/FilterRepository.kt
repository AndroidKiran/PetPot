package com.droid47.petfriend.search.domain.repositories

import com.droid47.petfriend.search.data.models.FilterItemEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FilterRepository {

    fun getAppliedFilterItemForSearch(): Single<List<FilterItemEntity>>

    fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>>

    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>>

    fun updateFilterItem(filterItemEntity: FilterItemEntity): Completable

    fun updateFilterForCategory(checked: Boolean, type: String): Completable

    fun resetFilter(categories: List<String>): Completable

    fun updateOrInsertTheFilter(filterItemEntity: FilterItemEntity): Completable

    fun updateLocationFilter(filterItemEntity: FilterItemEntity): Completable

    fun updateFilterOnApplied(categories: List<String>): Completable

    fun refreshFilter(list: List<FilterItemEntity>): Completable

    fun getFilterForFirstPage(name: String, type: String, selected: Boolean): Flowable<List<FilterItemEntity>>

    fun getFilterForTypes(types: List<String>, selected: Boolean): Flowable<List<FilterItemEntity>>

    fun getAppliedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>>

    fun updateFilterOnClosed(categories: List<String>): Completable
}