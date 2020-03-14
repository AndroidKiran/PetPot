package com.droid47.petgoogle.search.data.repos

import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.search.data.datasource.FilterItemDao
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.domain.repositories.FilterRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FilterRepo @Inject constructor(
    private val filterItemDao: FilterItemDao,
    private val localPreferencesRepository: LocalPreferencesRepository
    ) : FilterRepository {

    override fun fetchPageFilterOnUpdate(type: String): Flowable<FilterItemEntity> =
        filterItemDao.getPageFilterOnUpdate(type)

    override fun updateLastAppliedFilter(checked: Boolean, filterNameList: List<String>) =
        Completable.create { emitter ->
            try {
                filterItemDao.refreshLastAppliedFilter(checked, filterNameList)
                Completable.complete()
            } catch (exception: Exception) {
                Completable.error(exception)
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
        Single.create<FilterItemEntity> { emitter ->
            try {
                val filterItem = filterItemDao.getFilterItemForCategory(type, true)
                emitter.onSuccess(filterItem)
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateFilterItem(filterItemEntity: FilterItemEntity): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateFilterForItem(filterItemEntity)
                Completable.complete()
            } catch (exception: Exception) {
                Completable.error(exception)
            }
        }

    override fun updateFilterForCategory(checked: Boolean, type: String): Completable =
        Completable.create { emitter ->
            try {
                filterItemDao.updateSelection(checked, type)
                Completable.complete()
            } catch (exception: Exception) {
                Completable.error(exception)
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

    override fun getSearchLimit(): Int = localPreferencesRepository.fetchSearchLimit()
}