package com.droid47.petfriend.search.data.repos

import androidx.paging.DataSource
import com.droid47.petfriend.search.data.datasource.PetDao
import com.droid47.petfriend.search.data.datasource.PetTypeDao
import com.droid47.petfriend.search.data.datasource.SearchNetworkSource
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.search.SearchResponseEntity
import com.droid47.petfriend.search.data.models.type.BreedResponseEntity
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import com.droid47.petfriend.search.data.models.type.PetTypeResponseEntity
import com.droid47.petfriend.search.domain.repositories.PetRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PetRepo @Inject constructor(
    private val searchNetworkSource: SearchNetworkSource,
    private val petTypeDao: PetTypeDao,
    private val petDao: PetDao
) : PetRepository {

    override fun insertPetTypeToDB(petTypes: List<PetTypeEntity>): Single<List<PetTypeEntity>> =
        petTypeDao.insertPetTypeList(petTypes)
            .map { petTypes }

    override fun clearPetsFromDb(status: Boolean): Completable =
        petDao.deletePetsFor(status)

    override fun addPets(list: List<PetEntity>): Single<List<Long>> = petDao.insertPets(list)

    override fun fetchFavoritePetsFromDB(status: Boolean): DataSource.Factory<Int, PetEntity> =
        petDao.getFavoritePetsDataSource(status)

    override fun fetchRecentPetsFromDB(petType: String): DataSource.Factory<Int, PetEntity> =
        petDao.getRecentPetsDataSource(petType)

    override fun fetchNearByPetsFromDb(petType: String): DataSource.Factory<Int, PetEntity> =
        petDao.getNearByPetsDataSource(petType)

    override fun fetchAllPetsFromDb(): DataSource.Factory<Int, PetEntity> =
        petDao.getAllPetsDataSource()

    override fun updateFavoriteStatus(petEntity: PetEntity): Completable =
        petDao.updatePet(petEntity)

    override fun fetchPetFor(id: Int): Single<PetEntity> = petDao.getPetById(id)

    override fun subscribeToSelectedPet(id: Int): Flowable<PetEntity> =
        petDao.getSelectedPet(id)

    override fun fetchFavoritePets(status: Boolean): Single<List<PetEntity>> =
        petDao.getFavoritePetsSingle(status)

    override fun updateFavoritePetStatus(updateStatus: Boolean, currentStatus: Boolean): Completable =
        petDao.updatePetsTo(updateStatus, currentStatus)

    override fun fetchPetsFromNetWork(options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity> =
        searchNetworkSource.getPets(options)

    override fun fetchPetTypesFromNetwork(): Single<PetTypeResponseEntity> =
        searchNetworkSource.getPetTypes()

    override fun fetchBreedsFromNetwork(animalType: String): Single<BreedResponseEntity> =
        searchNetworkSource.getBreeds(animalType)
}