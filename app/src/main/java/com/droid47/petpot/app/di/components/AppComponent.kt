package com.droid47.petpot.app.di.components

import android.app.Application
import com.droid47.petpot.app.di.modules.AppInitializersBindingModule
import com.droid47.petpot.app.di.modules.AppModule
import com.droid47.petpot.app.di.modules.ViewModelModule
import com.droid47.petpot.app.domain.appInitializers.AppInitializers
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.home.presentation.di.HomeSubComponent
import com.droid47.petpot.launcher.presentation.di.AbstractLauncherModule
import com.droid47.petpot.launcher.presentation.di.LauncherSubComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AppSubComponents::class, ViewModelModule::class,
    AbstractLauncherModule::class, AppInitializersBindingModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun launcherComponent(): LauncherSubComponent.Factory
    fun homeComponent(): HomeSubComponent.Factory
    fun appServiceComponent(): AppServiceComponent.Factory

    fun sharedPreference(): LocalPreferencesRepository
    fun appInitializers(): AppInitializers
    fun activityLifecycleCallbacks(): Application.ActivityLifecycleCallbacks

}