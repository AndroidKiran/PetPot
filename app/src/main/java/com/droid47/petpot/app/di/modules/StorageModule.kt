package com.droid47.petpot.app.di.modules

import android.app.Application
import androidx.room.Room
import com.droid47.petpot.base.storage.db.PetDb
import com.droid47.petpot.organization.data.datasource.OrganizationDao
import com.droid47.petpot.search.data.datasource.PetDao
import com.droid47.petpot.search.data.datasource.PetFilterDao
import com.droid47.petpot.search.data.datasource.PetTypeDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

private const val DB_NAME = "petDB.db"

@Module
object StorageModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideDb(application: Application): PetDb =
        Room.databaseBuilder(application, PetDb::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @JvmStatic
    @Singleton
    fun providePetTypeDao(petDb: PetDb): PetTypeDao = petDb.getPetTypeDao()

    @Provides
    @JvmStatic
    @Singleton
    fun providePetDao(petDb: PetDb): PetDao = petDb.getPetDao()

    @Provides
    @JvmStatic
    @Singleton
    fun provideFilterItemDao(petDb: PetDb): PetFilterDao = petDb.getPetFilterDao()

    @Provides
    @JvmStatic
    @Singleton
    fun provideOrganizationDao(petDb: PetDb): OrganizationDao = petDb.getOrganizationDao()

}