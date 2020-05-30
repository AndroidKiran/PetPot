package com.droid47.petfriend.search.data.datasource

import androidx.room.*
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.SORT
import com.droid47.petfriend.search.presentation.models.FilterConstants.SORT_BY_DISTANCE
import com.droid47.petfriend.search.presentation.models.FilterConstants.SORT_BY_RECENT
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FilterItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filterItemEntity: FilterItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(filterItemEntities: List<FilterItemEntity>)

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_SELECTED}=:isSelected AND ${FilterItemEntity.COL_TYPE} IN(:categories)")
    fun getSelectedFilterItemsForCategories(categories: List<String>, isSelected: Boolean): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_FILTER_APPLIED}=:isSelected AND ${FilterItemEntity.COL_TYPE} IN(:categories)")
    fun getAppliedFilterItemsForCategories(categories: List<String>, isSelected: Boolean): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} =:type ORDER BY ${FilterItemEntity.COL_SELECTED} DESC")
    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} =:type")
    fun getFilterItemFor(type: String): FilterItemEntity?

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_FILTER_APPLIED}=:checked")
    fun getAppliedFilterItemForSearch(checked: Boolean): Single<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_NAME}=:name AND ${FilterItemEntity.COL_TYPE}=:type AND ${FilterItemEntity.COL_SELECTED}=:selected")
    fun getFilterForFirstPage(name: String, type: String, selected: Boolean): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} IN(:types) AND ${FilterItemEntity.COL_SELECTED}=:selected")
    fun getFilterForTypes(types: List<String>, selected: Boolean): Flowable<List<FilterItemEntity>>

    @Update
    fun updateFilterForItem(filterItemEntity: FilterItemEntity): Completable

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:checked WHERE ${FilterItemEntity.COL_TYPE}=:type")
    fun updateSelection(checked: Boolean, type: String): Completable

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:checked, ${FilterItemEntity.COL_FILTER_APPLIED}=:checked WHERE ${FilterItemEntity.COL_TYPE} IN(:categories)")
    fun resetFilters(checked: Boolean, categories: List<String>): Completable

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_NAME}=:name , ${FilterItemEntity.COL_SELECTED}=:selected, ${FilterItemEntity.COL_FILTER_APPLIED}=:appliedFilter WHERE ${FilterItemEntity.COL_TYPE} =:type")
    fun updateFilterItemFor(name: String, type: String, selected: Boolean, appliedFilter: Boolean)

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:currentAppliedStatus WHERE ${FilterItemEntity.COL_TYPE} IN(:categories) AND ${FilterItemEntity.COL_FILTER_APPLIED}=:currentAppliedStatus AND ${FilterItemEntity.COL_SELECTED}=:currentSelectedStatus")
    fun updateFiltersOnClose(categories: List<String>, currentAppliedStatus: Boolean, currentSelectedStatus: Boolean)

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_FILTER_APPLIED}=:currentSelectedStatus WHERE ${FilterItemEntity.COL_TYPE} IN(:categories) AND ${FilterItemEntity.COL_SELECTED}=:currentSelectedStatus AND ${FilterItemEntity.COL_FILTER_APPLIED}=:currentAppliedStatus")
    fun updateFiltersOnApply(categories: List<String>, currentSelectedStatus: Boolean, currentAppliedStatus: Boolean)

    @Query("DELETE FROM ${FilterItemEntity.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun insertOrUpdateFilterItem(filterItemEntity: FilterItemEntity) {
        try {
            val existingItem = getFilterItemFor(filterItemEntity.type)
            if (existingItem == null) {
                insertFilter(filterItemEntity)
            } else {
                updateFilterItemFor(
                    filterItemEntity.name,
                    filterItemEntity.type,
                    filterItemEntity.selected,
                    filterItemEntity.filterApplied
                )
            }
        } catch (exception: Exception) {
            insertFilter(filterItemEntity)
        }
    }

    @Transaction
    fun refreshFilter(filterItemEntities: List<FilterItemEntity>) {
        deleteAll()
        insertAll(filterItemEntities)
    }

    @Transaction
    fun updateLocationFilter(filterItemEntity: FilterItemEntity) {
        insertOrUpdateFilterItem(filterItemEntity)
        insertOrUpdateFilterItem(
            FilterItemEntity(
                if (filterItemEntity.name.isNotEmpty()) SORT_BY_DISTANCE else SORT_BY_RECENT,
                SORT,
                selected = true,
                filterApplied = true
            )
        )
    }

    @Transaction
    fun updateFilterOnApplied(categories: List<String>) {
        updateFiltersOnApply(categories, currentSelectedStatus = true, currentAppliedStatus = false)
        updateFiltersOnApply(categories, currentSelectedStatus = false, currentAppliedStatus = true)
    }

    @Transaction
    fun updateFilterOnClosed(categories: List<String>) {
        updateFiltersOnClose(categories, currentAppliedStatus = true, currentSelectedStatus = false)
        updateFiltersOnClose(categories, currentAppliedStatus = false, currentSelectedStatus = true)
    }
}