package com.droid47.petfriend.search.data.datasource

import androidx.paging.DataSource
import androidx.room.*
import com.droid47.petfriend.search.data.models.search.PetEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPet(petEntity: PetEntity)

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun getPetById(id: Int): PetEntity?

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_ID} =:id")
    fun listenToUpdateFor(id: Int): Flowable<List<PetEntity>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getBookmarkPetListSingle(): Single<List<PetEntity>>

    @Query("SELECT * FROM ${PetEntity.TableInfo.TABLE_NAME} WHERE ${PetEntity.TableInfo.COL_BOOK_MARK_STATUS}=1 ORDER BY ${PetEntity.TableInfo.COL_BOOK_MARK_AT} DESC")
    fun getBookmarkPetList(): DataSource.Factory<Int, PetEntity>

    @Delete
    fun deletePet(petEntity: PetEntity)

    @Query("DELETE FROM ${PetEntity.TableInfo.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun insertOrDelete(petEntity: PetEntity) {
        if (petEntity.bookmarkStatus) {
            insertPet(petEntity)
        } else {
            deletePet(petEntity)
        }
    }
}