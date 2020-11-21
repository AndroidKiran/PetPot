package com.droid47.petpot.home.presentation.di

import com.droid47.petpot.organization.data.datasource.OrganizationRepository
import com.droid47.petpot.organization.domain.repositories.IOrganizationRepository
import com.droid47.petpot.petDetails.data.PetDetailsRepo
import com.droid47.petpot.petDetails.domain.PetDetailsRepository
import com.droid47.petpot.search.data.repos.FilterRepo
import com.droid47.petpot.search.data.repos.PetRepo
import com.droid47.petpot.search.domain.repositories.FilterRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface AbstractHomeModule {

    @Binds
    @Reusable
    fun bindSearchRepository(searchRepository: PetRepo): PetRepository

    @Binds
    @Reusable
    fun bindFilterRepository(filterRepo: FilterRepo): FilterRepository

    @Binds
    @Reusable
    fun bindPetDetailsRepository(petDetailsRepo: PetDetailsRepo): PetDetailsRepository

    @Binds
    @Reusable
    fun bindOrganizationRepository(organizationRepository: OrganizationRepository): IOrganizationRepository
}