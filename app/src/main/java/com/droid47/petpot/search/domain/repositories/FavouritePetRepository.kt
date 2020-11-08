package com.droid47.petpot.search.domain.repositories

import androidx.paging.DataSource
import com.droid47.petpot.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FavouritePetRepository {

    fun fetchFavoritePetsFromDB(status: Boolean): DataSource.Factory<Int, PetEntity>

    fun updateFavoriteStatus(petEntity: PetEntity): Completable

    fun subscribeToSelectedPet(id: Int): Flowable<PetEntity>

    fun fetchFavoritePets(status: Boolean): Single<List<PetEntity>>

    fun updateFavoritePetStatus(updateStatus: Boolean, currentStatus: Boolean): Completable
}