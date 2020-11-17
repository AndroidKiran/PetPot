package com.droid47.petpot.base.extensions

import android.util.Log
import com.droid47.petpot.BuildConfig
import org.jetbrains.annotations.NonNls

// Override this for testing.
private const val DEVELOPER_MODE = false

fun isDeveloperMode(): Boolean {
    return DEVELOPER_MODE || BuildConfig.DEBUG
}

//    private fun isDebugApp(): Boolean {
//        return try {
//            val application: BaseApplication = BaseApplication.getBaseInstance()
//            val packageName: String = application.getPackageName()
//            packageName.endsWith(DEV_BUILD_SUFFIX)
//        } catch (e: Exception) { // Will crash in java test,
//            // kotlin.UninitializedPropertyAccessException:
//            // lateinit property baseInstance has not been initialized
//            false
//        }
//    }

fun logE(tag: String, msg: String, tr: Throwable) {
    if (!isDeveloperMode()) return
    Log.e(tag, msg, tr)
}

fun logE(@NonNls tag: String, @NonNls msg: String) {
    if (!isDeveloperMode()) return
    Log.e(tag, msg)
}

fun logW(tag: String, msg: String) {
    if (!isDeveloperMode()) return
    Log.w(tag, msg)
}

fun logD(@NonNls tag: String, @NonNls message: String) {
    if (!isDeveloperMode()) return
    Log.d(tag, message)
}

fun logD(@NonNls tag: String, @NonNls msg: String, tr: Throwable) {
    if (!isDeveloperMode()) return
    Log.d(tag, msg, tr)
}
