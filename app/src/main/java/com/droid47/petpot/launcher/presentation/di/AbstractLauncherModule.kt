package com.droid47.petpot.launcher.presentation.di

import com.droid47.petpot.app.domain.FirebaseManager
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.search.data.repos.FavouriteRepo
import com.droid47.petpot.search.data.repos.PetRepo
import com.droid47.petpot.search.data.repos.PetTypeRepo
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetRepository
import com.droid47.petpot.search.domain.repositories.PetTypeRepository
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
    fun bindFavouriteRepository(favouriteRepo: FavouriteRepo): FavouritePetRepository

    @Binds
    @Reusable
    fun bindFirebaseAnalytics(firebaseAnalytics: FirebaseManager): IFirebaseManager
}