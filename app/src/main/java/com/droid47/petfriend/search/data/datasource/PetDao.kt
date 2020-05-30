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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPets(list: List<PetEntity>): Single<List<Long>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getPetById(id: Int): Single<PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getSelectedPet(id: Int): Flowable<PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsSingle(status: Boolean): Single<List<PetEntity>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getFavoritePetsDataSource(status: Boolean): DataSource.Factory<Int, PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_TYPE} LIKE :petType ORDER BY datetime(${PetEntity.TableInfo.COL_PUBLISHED_AT}) DESC")
    fun getRecentPetsDataSource(
        petType: String
    ): DataSource.Factory<Int, PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_TYPE} LIKE :petType ORDER BY ${PetEntity.TableInfo.COL_DISTANCE} ASC")
    fun getNearByPetsDataSource(
        petType: String
    ): DataSource.Factory<Int, PetEntity>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} ORDER BY ${PetEntity.TableInfo.COL_PUBLISHED_AT} DESC")
    fun getAllPetsDataSource(): DataSource.Factory<Int, PetEntity>

    @Delete
    fun deletePet(petEntity: PetEntity): Completable

    @Update
    fun updatePet(petEntity: PetEntity): Completable

    @Query("DELETE FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status")
    fun deletePetsFor(status: Boolean): Completable

    @Query("UPDATE ${PetEntity.TableInfo.TABLE_NAME} SET ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:updateStatus WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:currentStatus")
    fun updatePetsTo(updateStatus: Boolean, currentStatus: Boolean): Completable

    @Query("DELETE FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=:status")
    fun deletePets(status: Boolean): Completable

}