package com.droid47.petfriend.search.data.repos

import com.droid47.petfriend.search.data.datasource.FilterItemDao
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.PAGE_NUM
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FilterRepo @Inject constructor(
    private val filterItemDao: FilterItemDao
) : FilterRepository {

    override fun fetchPageFilterOnUpdate(): Flowable<FilterItemEntity> =
        filterItemDao.getPageFilterOnUpdate(PAGE_NUM)

    override fun updateLastAppliedFilter(checked: Boolean, filterNameList: List<String>) =
        Completable.create { emitter ->
            try {
                filterItemDao.refreshLastAppliedFilter(checked, filterNameList)
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
        filterItemDao.getAppliedFilterItemForSearch()

    override fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>> =
        filterItemDao.getSelectedFilterItemsForCategories(categories)

    override fun getSelectedFilterItemsForCategory(type: String): Flowable<List<FilterItemEntity>> =
        filterItemDao.getSelectedFilterItemsForCategory(type)

    override fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>> =
        filterItemDao.getFilterItemsForSelectedCategory(type)

    override fun getFilterItemForCategory(type: String): Single<FilterItemEntity> =
        Single.create { emitter ->
            try {
                when (val filterItem = filterItemDao.getFilterItemForCategory(type, true)) {
                    null -> emitter.onError(IllegalStateException("FilterItem is null"))
                    else -> emitter.onSuccess(filterItem)
                }
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateFilterItem(filterItemEntity: FilterItemEntity): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateFilterForItem(filterItemEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateFilterForCategory(checked: Boolean, type: String): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateSelection(checked, type)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

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

    override fun updateSortFilter(filterItemEntity: FilterItemEntity): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateSortFilter(filterItemEntity)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun resetFilter(categories: List<String>): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateSelectionForCategories(false, categories)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
}