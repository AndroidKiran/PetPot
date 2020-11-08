package com.droid47.petpot.base.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.droid47.petpot.organization.data.datasource.OrganizationDao
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity
import com.droid47.petpot.search.data.datasource.FavoritePetDao
import com.droid47.petpot.search.data.datasource.PetDao
import com.droid47.petpot.search.data.datasource.PetFilterDao
import com.droid47.petpot.search.data.datasource.PetTypeDao
import com.droid47.petpot.search.data.models.PetFilterCheckableEntity
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.data.models.search.PetEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity
import com.droid47.petpot.search.data.models.type.PetTypeEntity

@Database(
    entities = [PetTypeEntity::class, SearchPetEntity::class, FavouritePetEntity::class,
        PetFilterCheckableEntity::class, OrganizationCheckableEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(PetDbConverter::class)
abstract class PetDb : RoomDatabase() {
    abstract fun getPetTypeDao(): PetTypeDao
    abstract fun getPetDao(): PetDao
    abstract fun getFavouritePetDao(): FavoritePetDao
    abstract fun getPetFilterDao(): PetFilterDao
    abstract fun getOrganizationDao(): OrganizationDao
}