package com.droid47.petgoogle.app.di.components

import com.droid47.petgoogle.base.firebase.PetFirebaseMessagingService
import dagger.Subcomponent

@Subcomponent
interface AppServiceComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AppServiceComponent
    }

    fun inject(service: PetFirebaseMessagingService)
}