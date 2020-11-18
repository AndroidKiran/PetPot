package com.droid47.petpot.search.domain.repositories

import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FilterRepository {

    fun getAppliedFilterItemForSearch(): Single<List<PetFilterCheckableEntity>>

    fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<PetFilterCheckableEntity>>

    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<PetFilterCheckableEntity>>

    fun updateFilterItem(petFilterEntity: PetFilterCheckableEntity): Completable

    fun updateFilterForCategory(checked: Boolean, type: String): Completable

    fun resetFilter(categories: List<String>): Completable

    fun updateOrInsertTheFilter(petFilterEntity: PetFilterCheckableEntity): Completable

    fun updateLocationFilter(petFilterEntity: PetFilterCheckableEntity): Completable

    fun updateFilterOnApplied(categories: List<String>): Completable

    fun refreshFilter(list: List<PetFilterCheckableEntity>): Completable

    fun listenToFilterTypes(types: List<String>, selected: Boolean): Flowable<List<PetFilterCheckableEntity>>

    fun getAppliedFilterItemsForCategories(categories: List<String>): Flowable<List<PetFilterCheckableEntity>>

    fun updateFilterOnClosed(categories: List<String>): Completable

}