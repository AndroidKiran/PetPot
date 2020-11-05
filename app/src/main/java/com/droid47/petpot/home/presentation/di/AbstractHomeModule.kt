package com.droid47.petpot.home.presentation.di

import com.droid47.petpot.organization.data.datasource.OrganizationRepository
import com.droid47.petpot.organization.domain.repositories.IOrganizationRepository
import com.droid47.petpot.petDetails.data.PetDetailsRepo
import com.droid47.petpot.petDetails.domain.PetDetailsRepository
import com.droid47.petpot.search.data.repos.FilterRepo
import com.droid47.petpot.search.domain.repositories.FilterRepository
import dagger.Binds
import dagger.Module

@Module
interface AbstractHomeModule {

    @Binds
    fun bindFilterRepository(filterRepo: FilterRepo): FilterRepository

    @Binds
    fun bindPetDetailsRepository(petDetailsRepo: PetDetailsRepo): PetDetailsRepository

    @Binds
    fun bindOrganizationRepository(organizationRepository: OrganizationRepository): IOrganizationRepository
}