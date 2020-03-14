package com.droid47.petgoogle.app.di

import com.droid47.petgoogle.app.PetApplication
import com.droid47.petgoogle.app.di.modules.ActivityBuilderModule
import com.droid47.petgoogle.app.di.modules.AppModule
import com.droid47.petgoogle.app.di.modules.FirebaseModule
import com.droid47.petgoogle.app.di.modules.ServiceBuilderModule
import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@ApplicationScope
@Component(modules = [AppModule::class, ActivityBuilderModule::class, ServiceBuilderModule::class,
    FirebaseModule::class, AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<PetApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<PetApplication>() {
        abstract override fun build(): AppComponent
    }
}