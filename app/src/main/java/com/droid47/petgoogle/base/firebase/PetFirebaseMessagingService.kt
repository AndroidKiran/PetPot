package com.droid47.petgoogle.base.firebase

import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import javax.inject.Inject

class PetFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesRepository: LocalPreferencesRepository

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        preferencesRepository.saveFcmToken(token)
    }
}