package com.droid47.petgoogle.base.firebase

import com.droid47.petgoogle.BuildConfig
import com.droid47.petgoogle.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import javax.inject.Inject

class FirebaseRemoteConfig @Inject constructor() : RemoteConfig {

    val config = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(
                if (BuildConfig.DEBUG) 0 else (60 * 8)
            ).build()
        )
        setDefaultsAsync(R.xml.remote_config_defaults)
    }
}