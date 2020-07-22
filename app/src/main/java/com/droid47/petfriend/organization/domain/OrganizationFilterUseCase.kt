package com.droid47.petfriend.organization.domain

import com.droid47.petfriend.organization.data.models.OrganizationFilter
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import javax.inject.Inject

class OrganizationFilterUseCase @Inject constructor(private val organizationRepository: IOrganizationRepository) {

    fun setOrganizationFilter(organizationFilter: OrganizationFilter) {
        organizationRepository.setOrganizationFilter(organizationFilter)
    }

    fun getOrganizationFilter(): OrganizationFilter {
        return organizationRepository.getOrganizationFilter()
    }
}

