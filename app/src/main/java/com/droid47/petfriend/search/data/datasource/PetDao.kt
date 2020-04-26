package com.droid47.petfriend.search.data.datasource

import androidx.paging.DataSource
import androidx.room.*
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPet(petEntity: PetEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPets(list: List<PetEntity>): Single<List<Long>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getPetById(id: Int): Single<PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun fetchPetsForFlowable(id: Int): Flowable<List<PetEntity>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsSingle(status: Boolean): Single<List<PetEntity>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsDataSource(status: Boolean): DataSource.Factory<Int, PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_PUBLISHED_AT} DESC")
    fun getRecentPetsDataSource(status: Boolean): DataSource.Factory<Int, PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_PUBLISHED_AT} DESC")
    fun getNearByPetsDataSource(status: Boolean): DataSource.Factory<Int, PetEntity>

    @Delete
    fun deletePet(petEntity: PetEntity): Completable

    @Update
    fun updatePet(petEntity: PetEntity): Completable

    @Query("DELETE FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status")
    fun deletePetsFor(status: Boolean): Single<Int>

}