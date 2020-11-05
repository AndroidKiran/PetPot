package com.droid47.petpot.app.di.modules

import com.droid47.petpot.app.domain.appInitializers.WorkManagerInitializer
import com.droid47.petpot.base.appinitializer.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface AppInitializersBindingModule {

    @Binds
    @IntoSet
    fun provideWorkManagerInitializer(initializer: WorkManagerInitializer): AppInitializer
}