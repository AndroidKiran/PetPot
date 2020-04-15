package com.droid47.petfriend.app.di.modules

import com.droid47.petfriend.base.firebase.Analytics
import com.droid47.petfriend.base.firebase.FirebaseAnalytics
import com.droid47.petfriend.base.firebase.FirebaseRemoteConfig
import com.droid47.petfriend.base.firebase.RemoteConfig
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface FirebaseModule {

    @Binds
    @Singleton
    fun bindFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Analytics

    @Binds
    @Singleton
    fun bindFirebaseRemoteConfig(firebaseRemoteConfig: FirebaseRemoteConfig): RemoteConfig
}