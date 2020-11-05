package com.droid47.petpot.organization.data.datasource

import androidx.paging.DataSource
import androidx.room.*
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_FILTER_APPLIED
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_NAME
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_SELECTED
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity.TableInfo.COL_STATE
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity.TableInfo.TABLE_NAME
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface OrganizationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrganizations(list: List<OrganizationCheckableEntity>): Single<List<Long>>

    @Update
    fun updateOrganization(organizationEntity: OrganizationCheckableEntity): Completable

    @Query("SELECT * FROM $TABLE_NAME WHERE $COL_NAME  LIKE :name AND $COL_STATE LIKE :state ORDER BY $COL_NAME ASC")
    fun getOrganizationDataSource(
        name: String,
        state: String
    ): DataSource.Factory<Int, OrganizationCheckableEntity>


    @Query("UPDATE $TABLE_NAME SET ${COL_SELECTED}=:currentAppliedStatus WHERE ${COL_FILTER_APPLIED}=:currentAppliedStatus AND ${COL_SELECTED}=:currentSelectedStatus")
    fun updateFiltersOnClose(
        currentAppliedStatus: Boolean,
        currentSelectedStatus: Boolean
    )

    @Query("UPDATE $TABLE_NAME SET ${COL_FILTER_APPLIED}=:currentSelectedStatus WHERE ${COL_SELECTED}=:currentSelectedStatus AND ${COL_FILTER_APPLIED}=:currentAppliedStatus")
    fun updateFiltersOnApply(
        currentSelectedStatus: Boolean,
        currentAppliedStatus: Boolean
    )

    @Query("UPDATE $TABLE_NAME SET ${COL_FILTER_APPLIED}=:currentAppliedStatus, ${COL_SELECTED}=:currentSelectedStatus")
    fun clearFilters(
        currentSelectedStatus: Boolean,
        currentAppliedStatus: Boolean
    ): Completable

    @Transaction
    fun updateFilterOnApplied() {
        updateFiltersOnApply(currentSelectedStatus = true, currentAppliedStatus = false)
        updateFiltersOnApply(currentSelectedStatus = false, currentAppliedStatus = true)
    }

    @Transaction
    fun updateFilterOnClosed() {
        updateFiltersOnClose(currentAppliedStatus = true, currentSelectedStatus = false)
        updateFiltersOnClose(currentAppliedStatus = false, currentSelectedStatus = true)
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE $COL_SELECTED =:isSelected ORDER BY $COL_NAME ASC")
    fun getSelectedOrganizations(isSelected: Boolean): Flowable<List<OrganizationCheckableEntity>>
}