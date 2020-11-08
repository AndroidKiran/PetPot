package com.droid47.petpot.search.data.datasource

import androidx.paging.DataSource
import androidx.room.*
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FavoritePetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouritePet(petEntity: FavouritePetEntity): Completable

    @Query("SELECT * FROM ${FavouritePetEntity.TABLE_NAME} ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsSingle(): Single<List<FavouritePetEntity>>

    @Query("SELECT * FROM ${FavouritePetEntity.TABLE_NAME} ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsDataSource(): DataSource.Factory<Int, FavouritePetEntity>

    @Delete
    fun deletePet(petEntity: FavouritePetEntity): Completable

    @Query("SELECT * FROM ${FavouritePetEntity.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getSelectedPet(id: Int): Flowable<List<FavouritePetEntity>>

    @Query("DELETE FROM ${FavouritePetEntity.TABLE_NAME}")
    fun deleteAll(): Completable
}