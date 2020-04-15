package com.droid47.petfriend.app.di.components

import android.app.Application
import com.droid47.petfriend.app.data.LocalPreferenceDataSource
import com.droid47.petfriend.app.di.modules.AppModule
import com.droid47.petfriend.app.di.modules.FirebaseModule
import com.droid47.petfriend.app.di.modules.ViewModelModule
import com.droid47.petfriend.home.presentation.di.HomeSubComponent
import com.droid47.petfriend.launcher.presentation.di.LauncherSubComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AppSubComponents::class, ViewModelModule::class, FirebaseModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun launcherComponent(): LauncherSubComponent.Factory
    fun homeComponent(): HomeSubComponent.Factory
    fun appServiceComponent(): AppServiceComponent.Factory

    fun sharedPreference(): LocalPreferenceDataSource
}