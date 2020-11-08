package com.droid47.petpot.search.data.repos

import androidx.paging.DataSource
import com.droid47.petpot.search.data.datasource.FavoritePetDao
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.domain.repositories.FavouritePetRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class FavouriteRepo @Inject constructor(private val favoritePetDao: FavoritePetDao) :
    FavouritePetRepository {

    override fun addFavouritePet(petEntity: FavouritePetEntity): Completable =
        favoritePetDao.insertFavouritePet(petEntity)

    override fun fetchFavoritePetsFromDB(): DataSource.Factory<Int, FavouritePetEntity> =
        favoritePetDao.getFavoritePetsDataSource()

    override fun subscribeToSelectedPet(id: Int): Flowable<List<FavouritePetEntity>> =
        favoritePetDao.getSelectedPet(id)

    override fun deleteFavouritePet(petEntity: FavouritePetEntity): Completable =
        favoritePetDao.deletePet(petEntity)

    override fun deleteAllFavouritePet(): Completable =
        favoritePetDao.deleteAll()

    override fun fetchFavoritePets(): Single<List<FavouritePetEntity>> =
        favoritePetDao.getFavoritePetsSingle()

}