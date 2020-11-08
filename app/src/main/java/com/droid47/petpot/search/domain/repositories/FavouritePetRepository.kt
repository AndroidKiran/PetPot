package com.droid47.petpot.search.domain.repositories

import androidx.paging.DataSource
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FavouritePetRepository {

    fun addFavouritePet(petEntity: FavouritePetEntity): Completable

    fun fetchFavoritePetsFromDB(): DataSource.Factory<Int, FavouritePetEntity>

    fun subscribeToSelectedPet(id: Int): Flowable<List<FavouritePetEntity>>

    fun fetchFavoritePets(): Single<List<FavouritePetEntity>>

    fun deleteFavouritePet(petEntity: FavouritePetEntity): Completable

    fun deleteAllFavouritePet(): Completable

}