package com.droid47.petgoogle.app.di.modules

import com.droid47.petgoogle.app.di.scopes.ApplicationScope
import com.droid47.petgoogle.base.firebase.Analytics
import com.droid47.petgoogle.base.firebase.FirebaseAnalytics
import com.droid47.petgoogle.base.firebase.FirebaseRemoteConfig
import com.droid47.petgoogle.base.firebase.RemoteConfig
import dagger.Binds
import dagger.Module

@Module
interface FirebaseModule {

    @Binds
    @ApplicationScope
    fun bindFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Analytics

    @Binds
    @ApplicationScope
    fun bindFirebaseRemoteConfig(firebaseRemoteConfig: FirebaseRemoteConfig): RemoteConfig
}