package com.droid47.petfriend.launcher.presentation.di

import com.droid47.petfriend.app.domain.FirebaseManager
import com.droid47.petfriend.base.firebase.IFirebaseManager
import com.droid47.petfriend.search.data.repos.PetTypeRepo
import com.droid47.petfriend.search.data.repos.PetRepo
import com.droid47.petfriend.search.domain.repositories.PetTypeRepository
import com.droid47.petfriend.search.domain.repositories.PetRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface AbstractLauncherModule {

    @Binds
    @Reusable
    fun bindSearchRepository(searchRepository: PetRepo): PetRepository

    @Binds
    @Reusable
    fun bindPetTypeRepository(petTypeRepo: PetTypeRepo): PetTypeRepository

    @Binds
    @Reusable
    fun bindFirebaseAnalytics(firebaseAnalytics: FirebaseManager): IFirebaseManager
}