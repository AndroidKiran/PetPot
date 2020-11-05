package com.droid47.petpot.search.data.datasource

import androidx.room.*
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.data.models.SORT
import com.droid47.petpot.search.presentation.models.PetFilterConstants.SORT_BY_DISTANCE
import com.droid47.petpot.search.presentation.models.PetFilterConstants.SORT_BY_RECENT
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PetFilterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(petFilterEntity: PetFilterCheckableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(petFilterEntities: List<PetFilterCheckableEntity>)

    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_SELECTED}=:isSelected AND ${PetFilterCheckableEntity.COL_TYPE} IN(:categories)")
    fun getSelectedFilterItemsForCategories(
        categories: List<String>,
        isSelected: Boolean
    ): Flowable<List<PetFilterCheckableEntity>>

    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:isSelected AND ${PetFilterCheckableEntity.COL_TYPE} IN(:categories)")
    fun getAppliedFilterItemsForCategories(
        categories: List<String>,
        isSelected: Boolean
    ): Flowable<List<PetFilterCheckableEntity>>

    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_TYPE} =:type ORDER BY ${PetFilterCheckableEntity.COL_SELECTED} DESC")
    fun getFilterItemsForSelectedCategory(type: String): Flowable<List<PetFilterCheckableEntity>>

    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_TYPE} =:type")
    fun getFilterItemFor(type: String): PetFilterCheckableEntity?

    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:checked")
    fun getAppliedFilterItemForSearch(checked: Boolean): Single<List<PetFilterCheckableEntity>>

    //    @Query("SELECT * FROM ${PetFilterEntity.TABLE_NAME} WHERE ${PetFilterEntity.COL_NAME}=:name AND ${PetFilterEntity.COL_TYPE}=:type AND ${PetFilterEntity.COL_SELECTED}=:selected")
//    fun getFilterForFirstPage(name: String, type: String, selected: Boolean): Flowable<List<PetFilterEntity>>
//
    @Query("SELECT * FROM ${PetFilterCheckableEntity.TABLE_NAME} WHERE ${PetFilterCheckableEntity.COL_TYPE} IN(:types) AND ${PetFilterCheckableEntity.COL_SELECTED}=:selected")
    fun getFilterForTypes(types: List<String>, selected: Boolean): Flowable<List<PetFilterCheckableEntity>>

    @Update
    fun updateFilterForItem(petFilterEntity: PetFilterCheckableEntity): Completable

    @Query("UPDATE ${PetFilterCheckableEntity.TABLE_NAME} SET ${PetFilterCheckableEntity.COL_SELECTED}=:checked WHERE ${PetFilterCheckableEntity.COL_TYPE}=:type")
    fun updateSelection(checked: Boolean, type: String): Completable

    @Query("UPDATE ${PetFilterCheckableEntity.TABLE_NAME} SET ${PetFilterCheckableEntity.COL_SELECTED}=:checked, ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:checked WHERE ${PetFilterCheckableEntity.COL_TYPE} IN(:categories)")
    fun resetFilters(checked: Boolean, categories: List<String>): Completable

    @Query("UPDATE ${PetFilterCheckableEntity.TABLE_NAME} SET ${PetFilterCheckableEntity.COL_NAME}=:name , ${PetFilterCheckableEntity.COL_SELECTED}=:selected, ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:appliedFilter WHERE ${PetFilterCheckableEntity.COL_TYPE} =:type")
    fun updateFilterItemFor(name: String, type: String, selected: Boolean, appliedFilter: Boolean)

    @Query("UPDATE ${PetFilterCheckableEntity.TABLE_NAME} SET ${PetFilterCheckableEntity.COL_SELECTED}=:currentAppliedStatus WHERE ${PetFilterCheckableEntity.COL_TYPE} IN(:categories) AND ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:currentAppliedStatus AND ${PetFilterCheckableEntity.COL_SELECTED}=:currentSelectedStatus")
    fun updateFiltersOnClose(
        categories: List<String>,
        currentAppliedStatus: Boolean,
        currentSelectedStatus: Boolean
    )

    @Query("UPDATE ${PetFilterCheckableEntity.TABLE_NAME} SET ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:currentSelectedStatus WHERE ${PetFilterCheckableEntity.COL_TYPE} IN(:categories) AND ${PetFilterCheckableEntity.COL_SELECTED}=:currentSelectedStatus AND ${PetFilterCheckableEntity.COL_FILTER_APPLIED}=:currentAppliedStatus")
    fun updateFiltersOnApply(
        categories: List<String>,
        currentSelectedStatus: Boolean,
        currentAppliedStatus: Boolean
    )

    @Query("DELETE FROM ${PetFilterCheckableEntity.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun insertOrUpdateFilterItem(petFilterEntity: PetFilterCheckableEntity) {
        try {
            val existingItem = getFilterItemFor(petFilterEntity.type ?: "")
            if (existingItem == null) {
                insertFilter(petFilterEntity)
            } else {
                updateFilterItemFor(
                    petFilterEntity.name ?: "",
                    petFilterEntity.type ?: "",
                    petFilterEntity.selected,
                    petFilterEntity.filterApplied
                )
            }
        } catch (exception: Exception) {
            insertFilter(petFilterEntity)
        }
    }

    @Transaction
    fun refreshFilter(petFilterEntities: List<PetFilterCheckableEntity>) {
        deleteAll()
        insertAll(petFilterEntities)
    }

    @Transaction
    fun updateLocationFilter(petFilterEntity: PetFilterCheckableEntity) {
        insertOrUpdateFilterItem(petFilterEntity)
        insertOrUpdateFilterItem(
            PetFilterCheckableEntity(
                if (petFilterEntity.name?.isNotEmpty() == true) SORT_BY_DISTANCE else SORT_BY_RECENT,
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