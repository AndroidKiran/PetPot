package com.droid47.petfriend.app.di.modules

import androidx.lifecycle.ViewModelProvider
import com.droid47.petfriend.app.di.DaggerAwareViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: DaggerAwareViewModelFactory): ViewModelProvider.Factory
}
