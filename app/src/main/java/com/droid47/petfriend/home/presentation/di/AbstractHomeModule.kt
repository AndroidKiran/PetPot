package com.droid47.petfriend.home.presentation.di

import com.droid47.petfriend.organization.data.datasource.OrganizationRepository
import com.droid47.petfriend.organization.domain.repositories.IOrganizationRepository
import com.droid47.petfriend.petDetails.data.PetDetailsRepo
import com.droid47.petfriend.petDetails.domain.PetDetailsRepository
import com.droid47.petfriend.search.data.repos.FilterRepo
import com.droid47.petfriend.search.domain.repositories.FilterRepository
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