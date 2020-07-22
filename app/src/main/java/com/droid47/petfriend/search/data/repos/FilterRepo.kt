package com.droid47.petfriend.search.data.repos

import com.droid47.petfriend.search.data.datasource.PetFilterDao
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FilterRepo @Inject constructor(
    private val petFilterDao: PetFilterDao
) : FilterRepository {

    override fun updateFilterOnApplied(categories: List<String>): Completable =
        Completable.create { emitter ->
            try {
                petFilterDao.updateFilterOnApplied(categories)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun refreshFilter(list: List<PetFilterCheckableEntity>): Completable =
        Completable.create { emitter ->
            try {
                petFilterDao.refreshFilter(list)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun getAppliedFilterItemForSearch(): Single<List<PetFilterCheckableEntity>> =
        petFilterDao.getAppliedFilterItemForSearch(true)

    override fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<PetFilterCheckableEntity>> =
        petFilterDao.getSelectedFilterItemsForCategories(categories, true)

    override fun getFilterItemsForSelectedCategory(type: String): Flowable<List<PetFilterCheckableEntity>> =
        petFilterDao.getFilterItemsForSelectedCategory(type)

    override fun updateFilterItem(petFilterEntity: PetFilterCheckableEntity): Completable =
        petFilterDao.updateFilterForItem(petFilterEntity)

    override fun updateFilterForCategory(checked: Boolean, type: String): Completable =
        petFilterDao.updateSelection(checked, type)

    override fun updateOrInsertTheFilter(petFilterEntity: PetFilterCheckableEntity): Completable =
        Completable.create { emitter ->
            try {
                petFilterDao.insertOrUpdateFilterItem(petFilterEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateLocationFilter(petFilterEntity: PetFilterCheckableEntity): Completable =
        Completable.create { emitter ->
            try {
                petFilterDao.updateLocationFilter(petFilterEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun resetFilter(categories: List<String>): Completable =
        petFilterDao.resetFilters(false, categories)

    override fun getFilterForTypes(
        types: List<String>,
        selected: Boolean
    ): Flowable<List<PetFilterCheckableEntity>> =
        petFilterDao.getFilterForTypes(types, selected)

    override fun getAppliedFilterItemsForCategories(categories: List<String>): Flowable<List<PetFilterCheckableEntity>> =
        petFilterDao.getAppliedFilterItemsForCategories(categories, true)

    override fun updateFilterOnClosed(categories: List<String>): Completable =
        Completable.create { emitter ->
            try {
                petFilterDao.updateFilterOnClosed(categories)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
}