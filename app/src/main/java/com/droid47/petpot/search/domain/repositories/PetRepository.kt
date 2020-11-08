package com.droid47.petpot.search.domain.repositories

import androidx.paging.DataSource
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.data.models.search.SearchResponseEntity
import io.reactivex.Completable
import io.reactivex.Single

interface PetRepository {

    fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity>

    fun clearPetsFromDb(status: Boolean): Completable

    fun addPets(list: List<PetEntity>): Single<List<Long>>

    fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, PetEntity>

    fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, PetEntity>

    fun fetchAllPetsFromDb(): DataSource.Factory<Int, PetEntity>

}