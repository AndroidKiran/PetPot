package com.droid47.petfriend.organization.data.datasource

import android.content.Context
import androidx.paging.DataSource
import com.droid47.petfriend.base.extensions.readJSONFromAsset
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.organization.data.models.OrganizationFilter
import com.droid47.petfriend.organization.data.models.OrganizationResponseEntity
import com.droid47.petfriend.organization.data.models.State
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import com.droid47.petfriend.search.data.datasource.SearchNetworkSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class OrganizationRepository @Inject constructor(
    private val networkSource: SearchNetworkSource,
    private val organisationDao: OrganizationDao,
    private val localPreferencesRepository: LocalPreferencesRepository
) : IOrganizationRepository {

    override fun fetchOrganizationsFromNetwork(options: Map<String, @JvmSuppressWildcards Any>): Single<OrganizationResponseEntity> {
        return networkSource.getOrganizations(options)
    }

    override fun addOrganizationsToDb(organizations: List<OrganizationCheckableEntity>): Single<List<Long>> {
        return organisationDao.insertOrganizations(organizations)
    }

    override fun fetchOrganizationsFromDb(
        name: String,
        state: String
    ): DataSource.Factory<Int, OrganizationCheckableEntity> {
        return organisationDao.getOrganizationDataSource(name, state)
    }

    override fun updateOrganization(organizationEntity: OrganizationCheckableEntity): Completable {
        return organisationDao.updateOrganization(organizationEntity)
    }

    override fun setOrganizationFilter(organizationFilter: OrganizationFilter) {
        localPreferencesRepository.saveOrganizationFilter(organizationFilter)
    }

    override fun getOrganizationFilter(): OrganizationFilter {
        return localPreferencesRepository.getOrganizationFilter()
    }

    override fun getStates(gson: Gson, context: Context): Flowable<List<State>> {
        return Flowable.fromCallable {
            try {
                val statesJson = context.readJSONFromAsset("states.json")
                val type = object : TypeToken<List<State>>() {}.type
                gson.fromJson<List<State>>(statesJson, type) ?: emptyList()
            } catch (exception: Exception) {
                emptyList<State>()
            }
        }
    }

    override fun updateFilterOnApplied(): Completable =
        Completable.create { emitter ->
            try {
                organisationDao.updateFilterOnApplied()
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun updateFilterOnClosed(): Completable =
        Completable.create { emitter ->
            try {
                organisationDao.updateFilterOnClosed()
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }

    override fun clearFilter(): Completable = organisationDao.clearFilters(
        currentSelectedStatus = false,
        currentAppliedStatus = false
    )

    override fun getSelectedOrganizations(isSelected: Boolean): Flowable<List<OrganizationCheckableEntity>> {
        return organisationDao.getSelectedOrganizations(isSelected)
    }
}