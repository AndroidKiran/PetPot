package com.droid47.petfriend.search.domain.repositories

import androidx.paging.DataSource
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.data.models.type.BreedResponseEntity
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.search.data.models.type.PetTypeResponseEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PetRepository {

    fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity>

    fun fetchPetTypesFromNetwork(): Single<PetTypeResponseEntity>

    fun fetchBreedsFromNetwork(animalType: String): Single<BreedResponseEntity>

    fun insertPetTypeToDB(petTypes: List<PetTypeEntity>): Single<List<PetTypeEntity>>

    fun clearPetsFromDb(status: Boolean): Completable

    fun addPets(list: List<PetEntity>): Single<List<Long>>

    fun fetchFavoritePetsFromDB(status: Boolean): DataSource.Factory<Int, PetEntity>

    fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, PetEntity>

    fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, PetEntity>

    fun fetchAllPetsFromDb(): DataSource.Factory<Int, PetEntity>

    fun updateFavoriteStatus(petEntity: PetEntity): Completable

    fun fetchPetFor(id: Int): Single<PetEntity>

    fun subscribeToSelectedPet(id: Int): Flowable<PetEntity>

    fun fetchFavoritePets(status: Boolean): Single<List<PetEntity>>

    fun updateFavoritePetStatus(updateStatus: Boolean, currentStatus: Boolean): Completable
}