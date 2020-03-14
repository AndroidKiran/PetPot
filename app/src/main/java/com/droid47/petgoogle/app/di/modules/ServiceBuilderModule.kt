package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.base.firebase.PetFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceBuilderModule {

    @ContributesAndroidInjector
    fun contributeFirebaseMessagingService(): PetFirebaseMessagingService
}