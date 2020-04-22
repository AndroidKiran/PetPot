package com.droid47.petfriend.app.di.modules

import com.droid47.petfriend.app.domain.appInitializers.StethoInitializer
import com.droid47.petfriend.app.domain.appInitializers.WorkManagerInitializer
import com.droid47.petfriend.base.appinitializer.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface AppInitializersBindingModule {

    @Binds
    @IntoSet
    fun provideStethoInitializer(initializer: StethoInitializer): AppInitializer

    @Binds
    @IntoSet
    fun provideWorkManagerInitializer(initializer: WorkManagerInitializer): AppInitializer
}