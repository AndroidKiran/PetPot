package com.droid47.petpot.search.domain.repositories

import androidx.paging.DataSource
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.data.models.search.SearchResponseEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PetRepository {

    fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity>

    fun addPets(list: List<SearchPetEntity>): Single<List<Long>>

    fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, SearchPetEntity>

    fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, SearchPetEntity>

    fun fetchAllPetsFromDb(): DataSource.Factory<Int, SearchPetEntity>

    fun fetchPetFor(id: Int): Single<SearchPetEntity>

    fun subscribeToSelectedPet(id: Int): Flowable<List<SearchPetEntity>>

    fun clearAllPetsFromDb(): Completable
}