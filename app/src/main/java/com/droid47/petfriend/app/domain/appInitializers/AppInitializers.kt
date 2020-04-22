package com.droid47.petfriend.app.domain.appInitializers

import android.app.Application
import com.droid47.petfriend.base.appinitializer.AppInitializer
import javax.inject.Inject

class AppInitializers @Inject constructor(
    private val initializerSet: Set<@JvmSuppressWildcards AppInitializer>
) {
    fun init(application: Application) {
        initializerSet.forEach { appInitializer ->
            appInitializer.init(application)
        }
    }
}