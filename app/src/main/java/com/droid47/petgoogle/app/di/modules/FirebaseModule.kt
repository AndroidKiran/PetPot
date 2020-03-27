package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.base.firebase.Analytics
import com.droid47.petgoogle.base.firebase.FirebaseAnalytics
import com.droid47.petgoogle.base.firebase.FirebaseRemoteConfig
import com.droid47.petgoogle.base.firebase.RemoteConfig
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