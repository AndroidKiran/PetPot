package com.droid47.petfriend.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.droid47.petfriend.search.data.datasource.FilterItemDao
import com.droid47.petfriend.search.data.datasource.PetDao
import com.droid47.petfriend.search.data.datasource.PetTypeDao
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.search.PetEntity
import com.droid47.petfriend.search.data.models.type.PetTypeEntity

@Database(
    entities = [PetTypeEntity::class, PetEntity::class, FilterItemEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(PetDbConverter::class)
abstract class PetDb : RoomDatabase() {
    abstract fun getPetTypeDao(): PetTypeDao
    abstract fun getStarPetDao(): PetDao
    abstract fun getFilterItemDao(): FilterItemDao
}