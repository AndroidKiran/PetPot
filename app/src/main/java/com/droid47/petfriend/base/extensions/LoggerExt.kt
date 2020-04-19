package com.droid47.petfriend.base.extensions

import android.util.Log
import com.droid47.petfriend.BuildConfig
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

fun e(tag: String, msg: String, tr: Throwable) {
    if (isDeveloperMode()) {
        Log.e(tag, msg, tr)
    }
}

fun e(@NonNls tag: String, @NonNls msg: String) {
    if (isDeveloperMode()) {
        Log.e(tag, msg)
    }
}

fun w(tag: String, msg: String) {
    if (isDeveloperMode()) {
        Log.w(tag, msg)
    }
}

fun d(@NonNls tag: String, @NonNls message: String) {
    if (isDeveloperMode()) {
        Log.d(tag, message)
    }
}

fun d(@NonNls tag: String, @NonNls msg: String, tr: Throwable) {
    if (isDeveloperMode()) {
        Log.d(tag, msg, tr)
    }
}
