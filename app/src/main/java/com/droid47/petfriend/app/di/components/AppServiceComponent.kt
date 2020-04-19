package com.droid47.petfriend.app.di.components

import com.droid47.petfriend.base.firebase.PetFirebaseMessagingService
import dagger.Subcomponent
import javax.inject.Singleton

@Subcomponent
interface AppServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AppServiceComponent
    }

    fun inject(service: PetFirebaseMessagingService)
}