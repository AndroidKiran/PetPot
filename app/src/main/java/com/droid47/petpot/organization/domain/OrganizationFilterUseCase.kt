package com.droid47.petpot.organization.domain

import com.droid47.petpot.organization.data.models.OrganizationFilter
import com.droid47.petpot.organization.domain.repositories.IOrganizationRepository
import javax.inject.Inject

class OrganizationFilterUseCase @Inject constructor(private val organizationRepository: IOrganizationRepository) {

    fun setOrganizationFilter(organizationFilter: OrganizationFilter) {
        organizationRepository.setOrganizationFilter(organizationFilter)
    }

    fun getOrganizationFilter(): OrganizationFilter {
        return organizationRepository.getOrganizationFilter()
    }
}

