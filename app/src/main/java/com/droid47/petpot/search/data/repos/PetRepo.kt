package com.droid47.petpot.search.data.repos

import androidx.paging.DataSource
import com.droid47.petpot.search.data.datasource.PetDao
import com.droid47.petpot.search.data.datasource.PetTypeDao
import com.droid47.petpot.search.data.datasource.SearchNetworkSource
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.data.models.search.SearchResponseEntity
import com.droid47.petpot.search.data.models.type.BreedResponseEntity
import com.droid47.petpot.search.data.models.type.PetTypeEntity
import com.droid47.petpot.search.data.models.type.PetTypeResponseEntity
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PetRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource,
    private val petDao: PetDao
) : PetRepository {

    override fun clearPetsFromDb(status: Boolean): Completable =
        petDao.deletePetsFor(status)

    override fun addPets(list: List<PetEntity>): Single<List<Long>> = petDao.insertPets(list)

    override fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, PetEntity> =
        petDao.getRecentPetsDataSource(petType, false)

    override fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, PetEntity> =
        petDao.getNearByPetsDataSource(petType, false)

    override fun fetchAllPetsFromDb(): DataSource.Factory<Int, PetEntity> =
        petDao.getAllPetsDataSource()

    override fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity> =
        searchNetworkSource.getPets(options)
}