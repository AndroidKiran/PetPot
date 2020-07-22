package com.droid47.petfriend.base.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.droid47.petfriend.organization.data.datasource.OrganizationDao
import com.droid47.petfriend.organization.data.models.OrganizationCheckableEntity
import com.droid47.petfriend.search.data.datasource.PetFilterDao
import com.droid47.petfriend.search.data.datasource.PetDao
import com.droid47.petfriend.search.data.datasource.PetTypeDao
import com.droid47.petfriend.search.data.models.PetFilterCheckableEntity
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.type.PetTypeEntity

@Database(
    entities = [PetTypeEntity::class, PetEntity::class, PetFilterCheckableEntity::class, OrganizationCheckableEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(PetDbConverter::class)
abstract class PetDb : RoomDatabase() {
    abstract fun getPetTypeDao(): PetTypeDao
    abstract fun getPetDao(): PetDao
    abstract fun getPetFilterDao(): PetFilterDao
    abstract fun getOrganizationDao(): OrganizationDao
}