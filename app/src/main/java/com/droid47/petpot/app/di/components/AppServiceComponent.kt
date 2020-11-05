package com.droid47.petpot.app.di.components

import com.droid47.petpot.base.firebase.PetFirebaseMessagingService
import dagger.Subcomponent

@Subcomponent
interface AppServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AppServiceComponent
    }

    fun inject(service: PetFirebaseMessagingService)
}