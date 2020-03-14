package com.droid47.petgoogle.app.di.modules

import android.app.Application
import androidx.room.Room
import com.droid47.petgoogle.app.db.PetDb
import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import com.droid47.petgoogle.search.data.datasource.FilterItemDao
import com.droid47.petgoogle.search.data.datasource.PetDao
import com.droid47.petgoogle.search.data.datasource.PetTypeDao
import dagger.Module
import dagger.Provides

@Module
object StorageModule {

    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideDb(application: Application): PetDb =
        Room.databaseBuilder(application, PetDb::class.java, "petDB.db")
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @JvmStatic
    @ApplicationScope
    fun providePetTypeDao(petDb: PetDb): PetTypeDao = petDb.getPetTypeDao()

    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideStarPeteDao(petDb: PetDb): PetDao = petDb.getStarPetDao()

    @Provides
    @JvmStatic
    @ApplicationScope
    fun provideFilterItemDao(petDb: PetDb): FilterItemDao = petDb.getFilterItemDao()
}