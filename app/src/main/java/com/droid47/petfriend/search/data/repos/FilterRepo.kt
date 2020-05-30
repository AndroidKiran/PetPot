package com.droid47.petfriend.search.data.repos

import com.droid47.petfriend.search.data.datasource.FilterItemDao
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FilterRepo @Inject constructor(
    private val filterItemDao: FilterItemDao
) : FilterRepository {

    override fun updateFilterOnApplied(categories: List<String>): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateFilterOnApplied(categories)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun refreshFilter(list: List<FilterItemEntity>): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.refreshFilter(list)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun getAppliedFilterItemForSearch(): Single<List<FilterItemEntity>> =
        filterItemDao.getAppliedFilterItemForSearch(true)

    override fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>> =
        filterItemDao.getSelectedFilterItemsForCategories(categories, true)

    override fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>> =
        filterItemDao.getFilterItemsForSelectedCategory(type)

    override fun updateFilterItem(filterItemEntity: FilterItemEntity): Completable =
        filterItemDao.updateFilterForItem(filterItemEntity)

    override fun updateFilterForCategory(checked: Boolean, type: String): Completable =
        filterItemDao.updateSelection(checked, type)

    override fun updateOrInsertTheFilter(filterItemEntity: FilterItemEntity): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.insertOrUpdateFilterItem(filterItemEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateLocationFilter(filterItemEntity: FilterItemEntity): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateLocationFilter(filterItemEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun resetFilter(categories: List<String>): Completable =
        filterItemDao.resetFilters(false, categories)

    override fun getFilterForFirstPage(
        name: String,
        type: String,
        selected: Boolean
    ): Flowable<List<FilterItemEntity>> =
        filterItemDao.getFilterForFirstPage(name, type, selected)

    override fun getFilterForTypes(
        types: List<String>,
        selected: Boolean
    ): Flowable<List<FilterItemEntity>> =
        filterItemDao.getFilterForTypes(types, selected)

    override fun getAppliedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>> =
        filterItemDao.getAppliedFilterItemsForCategories(categories, true)

    override fun updateFilterOnClosed(categories: List<String>): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateFilterOnClosed(categories)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
}