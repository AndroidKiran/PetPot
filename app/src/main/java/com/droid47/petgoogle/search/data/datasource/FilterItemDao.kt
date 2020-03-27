package com.droid47.petgoogle.search.data.datasource

import androidx.room.*
import com.droid47.petgoogle.search.data.models.FilterItemEntity
import com.droid47.petgoogle.search.data.models.PAGE_NUM
import com.droid47.petgoogle.search.data.models.SORT
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FilterItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filterItemEntity: FilterItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(filterItemEntities: List<FilterItemEntity>)

    @Query("DELETE FROM ${FilterItemEntity.TABLE_NAME}")
    fun deleteAll()

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_SELECTED}=1 AND ${FilterItemEntity.COL_TYPE} IN(:categories)")
    fun getSelectedFilterItemsForCategories(categories: List<String>): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_SELECTED}=1 AND ${FilterItemEntity.COL_TYPE} =:type")
    fun getSelectedFilterItemsForCategory(type: String): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} =:type ORDER BY ${FilterItemEntity.COL_SELECTED} DESC")
    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<FilterItemEntity>>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} =:type AND ${FilterItemEntity.COL_SELECTED}=:checked")
    fun getFilterItemForCategory(type: String, checked: Boolean): FilterItemEntity?

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE} =:type")
    fun getFilterItemFor(type: String): FilterItemEntity?

    @Query("DELETE FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE}=:type")
    fun deleteFilterByType(type: String)

    @Update
    fun updateFilterForItem(filterItemEntity: FilterItemEntity)

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:checked WHERE ${FilterItemEntity.COL_TYPE}=:type")
    fun updateSelection(checked: Boolean, type: String)

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:checked WHERE ${FilterItemEntity.COL_TYPE} IN(:categories)")
    fun updateSelectionForCategories(checked: Boolean, categories: List<String>)

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_NAME}=:name, ${FilterItemEntity.COL_SELECTED}=:selected WHERE ${FilterItemEntity.COL_TYPE} =:type")
    fun updateFilterItemFor(name: String, type: String, selected: Boolean)

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
                    filterItemEntity.selected
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
        insertOrUpdateFilterItem(FilterItemEntity("recent", SORT, true))
        insertOrUpdateFilterItem(FilterItemEntity(1.toString(), PAGE_NUM, true))
    }

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_TYPE}=:type AND ${FilterItemEntity.COL_SELECTED}=1")
    fun getPageFilterOnUpdate(type: String): Flowable<FilterItemEntity>

    @Query("SELECT * FROM ${FilterItemEntity.TABLE_NAME} WHERE ${FilterItemEntity.COL_SELECTED}=1")
    fun getAppliedFilterItemForSearch(): Single<List<FilterItemEntity>>

    @Query("UPDATE ${FilterItemEntity.TABLE_NAME} SET ${FilterItemEntity.COL_SELECTED}=:checked WHERE ${FilterItemEntity.COL_NAME} IN(:filterNameList)")
    fun updateLastAppliedFilter(checked: Boolean, filterNameList: List<String>)

    @Transaction
    fun refreshLastAppliedFilter(checked: Boolean, filterNameList: List<String>) {
        updateSelection(false, PAGE_NUM)
        updateLastAppliedFilter(checked, filterNameList)
    }

    @Transaction
    fun updateSortFilter(filterItemEntity: FilterItemEntity) {
        insertOrUpdateFilterItem(filterItemEntity)
        insertOrUpdateFilterItem(FilterItemEntity(1.toString(), PAGE_NUM, true))
    }

}