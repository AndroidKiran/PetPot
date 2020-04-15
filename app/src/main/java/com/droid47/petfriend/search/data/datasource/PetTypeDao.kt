package com.droid47.petfriend.search.data.datasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.droid47.petfriend.search.data.models.type.PetTypeEntity
import io.reactivex.Single

@Dao
interface PetTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPetTypeList(petTypeEntityList: List<PetTypeEntity>): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPetType(animalTypeEntity: PetTypeEntity): Single<Long>

    @Query("SELECT ${PetTypeEntity.COL_NAME} FROM ${PetTypeEntity.TABLE_NAME} ORDER BY ${PetTypeEntity.COL_ANIMAL_ORDER} ASC")
    fun getPetNamesLiveData(): LiveData<List<String>>

    @Query("SELECT ${PetTypeEntity.COL_NAME} FROM ${PetTypeEntity.TABLE_NAME} ORDER BY ${PetTypeEntity.COL_ANIMAL_ORDER} ASC")
    fun getPetNames(): Single<List<String>>

    @Query("SELECT * FROM ${PetTypeEntity.TABLE_NAME}")
    fun getPetTypeList(): Single<List<PetTypeEntity>>

    @Query("UPDATE ${PetTypeEntity.TABLE_NAME} SET ${PetTypeEntity.COL_SELECTED} = 1 WHERE ${PetTypeEntity.COL_NAME} =:animalType")
    fun updateSelectedPetType(animalType: String)

    @Query("UPDATE ${PetTypeEntity.TABLE_NAME} SET ${PetTypeEntity.COL_SELECTED} = 0 WHERE ${PetTypeEntity.COL_NAME} !=:animalType")
    fun updateUnSelectedPetType(animalType: String)

    @Transaction
    fun updateSelectedAnimalItem(animalType: String) {
        updateUnSelectedPetType(animalType)
        updateSelectedPetType(animalType)
    }

    @Query("SELECT * FROM ${PetTypeEntity.TABLE_NAME} WHERE ${PetTypeEntity.COL_SELECTED} = 1")
    fun getSelectedPet(): PetTypeEntity

}