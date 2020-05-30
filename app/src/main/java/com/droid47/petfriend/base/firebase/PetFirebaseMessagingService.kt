package com.droid47.petfriend.base.firebase

import com.droid47.petfriend.app.PetApplication
import com.droid47.petfriend.base.storage.LocalPreferencesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class PetFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesRepository: LocalPreferencesRepository

    override fun onCreate() {
        injectComponent()
        super.onCreate()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        preferencesRepository.saveFcmToken(token)
    }

    private fun injectComponent() {
        val appServiceComponent =
            (application as PetApplication).appComponent.appServiceComponent().create()
        appServiceComponent.inject(this)
    }
}