package com.droid47.petpot.organization.domain.repositories

import android.content.Context
import androidx.paging.DataSource
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.organization.data.models.OrganizationFilter
import com.droid47.petpot.organization.data.models.OrganizationResponseEntity
import com.droid47.petpot.organization.data.models.State
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface IOrganizationRepository {
    fun fetchOrganizationsFromNetwork(options: Map<String, @JvmSuppressWildcards Any>): Single<OrganizationResponseEntity>
    fun addOrganizationsToDb(organizations: List<OrganizationCheckableEntity>): Single<List<Long>>
    fun fetchOrganizationsFromDb(
        name: String,
        state: String
    ): DataSource.Factory<Int, OrganizationCheckableEntity>

    fun updateOrganization(organizationEntity: OrganizationCheckableEntity): Completable
    fun setOrganizationFilter(organizationFilter: OrganizationFilter)
    fun getOrganizationFilter(): OrganizationFilter
    fun getStates(gson: Gson, context: Context): Flowable<List<State>>
    fun updateFilterOnApplied(): Completable
    fun updateFilterOnClosed(): Completable
    fun clearFilter(): Completable
    fun getSelectedOrganizations(isSelected: Boolean): Flowable<List<OrganizationCheckableEntity>>
}