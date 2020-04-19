package com.droid47.petfriend.home.presentation.di

import com.droid47.petfriend.bookmark.data.BookmarkRepo
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.petDetails.data.PetDetailsRepo
import com.droid47.petfriend.petDetails.domain.PetDetailsRepository
import com.droid47.petfriend.search.data.repos.FilterRepo
import com.droid47.petfriend.search.data.repos.PetTypeRepo
import com.droid47.petfriend.search.data.repos.SearchRepo
import com.droid47.petfriend.search.domain.repositories.FilterRepository
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.domain.repositories.SearchRepository
import dagger.Binds
import dagger.Module

@Module
interface AbstractHomeModule {

    @Binds
    fun bindFilterRepository(filterRepo: FilterRepo): FilterRepository

    @Binds
    fun bindPetDetailsRepository(petDetailsRepo: PetDetailsRepo): PetDetailsRepository
}