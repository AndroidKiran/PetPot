package com.droid47.petfriend.app.di.modules

import android.app.Application
import androidx.room.Room
import com.droid47.petfriend.app.db.PetDb
import com.droid47.petfriend.search.data.datasource.FilterItemDao
import com.droid47.petfriend.search.data.datasource.PetDao
import com.droid47.petfriend.search.data.datasource.PetTypeDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object StorageModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideDb(application: Application): PetDb =
        Room.databaseBuilder(application, PetDb::class.java, "petDB.db")
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @JvmStatic
    @Singleton
    fun providePetTypeDao(petDb: PetDb): PetTypeDao = petDb.getPetTypeDao()

    @Provides
    @JvmStatic
    @Singleton
    fun provideStarPeteDao(petDb: PetDb): PetDao = petDb.getStarPetDao()

    @Provides
    @JvmStatic
    @Singleton
    fun provideFilterItemDao(petDb: PetDb): FilterItemDao = petDb.getFilterItemDao()

}