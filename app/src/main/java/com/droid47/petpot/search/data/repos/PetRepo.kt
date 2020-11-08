package com.droid47.petpot.search.data.repos

import androidx.paging.DataSource
import com.droid47.petpot.search.data.datasource.PetDao
import com.droid47.petpot.search.data.datasource.SearchNetworkSource
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.data.models.search.SearchResponseEntity
import com.droid47.petpot.search.domain.repositories.PetRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PetRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource,
    private val petDao: PetDao
) : PetRepository {

    override fun addPets(list: List<SearchPetEntity>): Single<List<Long>> = petDao.insertPets(list)


    override fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, SearchPetEntity> =
        petDao.getRecentPetsDataSource(petType)

    override fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, SearchPetEntity> =
        petDao.getNearByPetsDataSource(petType)

    override fun fetchAllPetsFromDb(): DataSource.Factory<Int, SearchPetEntity> =
        petDao.getAllPetsDataSource()

    override fun fetchPetFor(id: Int): Single<SearchPetEntity> = petDao.getPetById(id)

    override fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity> =
        searchNetworkSource.getPets(options)

    override fun subscribeToSelectedPet(id: Int): Flowable<List<SearchPetEntity>> =
        petDao.getSelectedPet(id)

    override fun clearAllPetsFromDb(): Completable = petDao.deleteAll()

}