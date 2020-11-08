package com.droid47.petpot.search.data.repos

import androidx.paging.DataSource
import com.droid47.petpot.search.data.datasource.PetDao
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FavouritePetRepo @Inject constructor(private val petDao: PetDao) : FavouritePetRepository {

    override fun fetchFavoritePetsFromDB(status: Boolean): DataSource.Factory<Int, PetEntity> =
        petDao.getFavoritePetsDataSource(status)

    override fun updateFavoriteStatus(petEntity: PetEntity): Completable =
        petDao.updatePet(petEntity)

    override fun subscribeToSelectedPet(id: Int): Flowable<PetEntity> =
        petDao.getSelectedPet(id)

    override fun fetchFavoritePets(status: Boolean): Single<List<PetEntity>> =
        petDao.getFavoritePetsSingle(status)

    override fun updateFavoritePetStatus(
        updateStatus: Boolean,
        currentStatus: Boolean
    ): Completable =
        petDao.updatePetsTo(updateStatus, currentStatus)
}