package com.droid47.petfriend.app.di.components

import com.droid47.petfriend.base.firebase.PetFirebaseMessagingService
import dagger.Subcomponent

@Subcomponent
interface AppServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AppServiceComponent
    }

    fun inject(service: PetFirebaseMessagingService)
}