package com.droid47.petpot.app

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.droid47.petpot.app.di.components.AppComponent
import com.droid47.petpot.app.di.components.DaggerAppComponent
import com.droid47.petpot.app.di.modules.GlideApp
import com.droid47.petpot.base.extensions.applyTheme
import com.droid47.petpot.base.extensions.isDeveloperMode
import com.droid47.petpot.base.firebase.CrashlyticsExt

class PetApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this@PetApplication)
    }

    private val localSharedPreferences = appComponent.sharedPreference()
    private val activityLifecycleCallbacks = appComponent.activityLifecycleCallbacks()
    private val appInitializers = appComponent.appInitializers()

    init {
       loadNativeLib()
    }

    override fun onCreate() {
        super.onCreate()
        checkAndEnableStrictMode()
        appInitializers.init(this@PetApplication)
        activateTheme()
        registerActivityLifecycleCallbacks()
    }

    override fun onTerminate() {
        unRegisterActivityLifecycleCallbacks()
        super.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlideApp.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this@PetApplication).trimMemory(level)
    }

    private fun activateTheme() {
        applyTheme(localSharedPreferences.fetchAppliedTheme())
    }

    private fun registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun unRegisterActivityLifecycleCallbacks() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun checkAndEnableStrictMode() {
        if (isDeveloperMode()) {
            val builder = VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .penaltyLog()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.detectContentUriWithoutPermission()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.detectNonSdkApiUsage()
            }
            StrictMode.setVmPolicy(builder.build())
        }
    }

    private fun loadNativeLib() {
        try {
            System.loadLibrary("native-lib");
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
        }
    }

}