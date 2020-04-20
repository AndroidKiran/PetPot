package com.droid47.petfriend.launcher.presentation.di

import com.droid47.petfriend.bookmark.data.BookmarkRepo
import com.droid47.petfriend.bookmark.domain.repositories.BookmarkRepository
import com.droid47.petfriend.search.data.repos.PetTypeRepo
import com.droid47.petfriend.search.data.repos.SearchRepo
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.domain.repositories.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface AbstractLauncherModule {

    @Binds
    @Reusable
    fun bindSearchRepository(searchRepository: SearchRepo): SearchRepository

    @Binds
    @Reusable
    fun bindPetTypeRepository(petTypeRepo: PetTypeRepo): PetTypeRepository

    @Binds
    @Reusable
    fun bindBookmarkRepository(bookmarkRepo: BookmarkRepo): BookmarkRepository
}