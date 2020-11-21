package com.droid47.petpot.app.di.modules

import com.droid47.petpot.app.domain.FirebaseManager
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.search.data.repos.FavouritePetRepo
import com.droid47.petpot.search.data.repos.PetTypeRepo
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import com.droid47.petpot.search.domain.repositories.PetTypeRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
interface AbstractBaseModule {

    @Binds
    @Reusable
    fun bindFavouriteRepository(favouritePetRepo: FavouritePetRepo): FavouritePetRepository

    @Binds
    @Reusable
    fun bindPetTypeRepository(petTypeRepo: PetTypeRepo): PetTypeRepository

    @Binds
    @Reusable
    fun bindFirebaseAnalytics(firebaseAnalytics: FirebaseManager): IFirebaseManager
}