package com.droid47.petpot.base.extensions

import android.util.Log
import com.droid47.petpot.BuildConfig
import org.jetbrains.annotations.NonNls

fun isDeveloperMode(): Boolean {
    return BuildConfig.DEBUG
}

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
