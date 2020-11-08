package com.droid47.petpot.search.data.datasource

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPet(petEntity: SearchPetEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPets(list: List<SearchPetEntity>): Single<List<Long>>

    @Query("SELECT * FROM ${SearchPetEntity.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getPetById(id: Int): Single<SearchPetEntity>

    @Query("SELECT * FROM ${SearchPetEntity.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_TYPE} LIKE :petType ORDER BY datetime(${PetEntity.TableInfo.COL_PUBLISHED_AT}) DESC")
    fun getRecentPetsDataSource(
        petType: String
    ): DataSource.Factory<Int, SearchPetEntity>

    @Query("SELECT * FROM ${SearchPetEntity.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_TYPE} LIKE :petType ORDER BY ${PetEntity.TableInfo.COL_DISTANCE} ASC")
    fun getNearByPetsDataSource(
        petType: String
    ): DataSource.Factory<Int, SearchPetEntity>

    @Query("SELECT * FROM ${SearchPetEntity.TABLE_NAME} ORDER BY ${PetEntity.TableInfo.COL_PUBLISHED_AT} DESC")
    fun getAllPetsDataSource(): DataSource.Factory<Int, SearchPetEntity>

    @Query("SELECT * FROM ${SearchPetEntity.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getSelectedPet(id: Int): Flowable<List<SearchPetEntity>>

    @Query("DELETE FROM ${SearchPetEntity.TABLE_NAME}")
    fun deleteAll(): Completable

}