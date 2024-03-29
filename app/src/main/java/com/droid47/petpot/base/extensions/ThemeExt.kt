package com.droid47.petpot.base.extensions

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.droid47.petpot.base.storage.LocalPreferencesRepository

const val LIGHT_MODE = "light"
const val DARK_MODE = "dark"
const val DEFAULT_MODE = "default"

fun applyTheme(
    themeMode: String, localPreferencesRepository: LocalPreferencesRepository? = null,
    block: ((mode: String) -> Unit)? = null
) {
    val mode = when (themeMode) {
        LIGHT_MODE -> AppCompatDelegate.MODE_NIGHT_NO
        DARK_MODE -> AppCompatDelegate.MODE_NIGHT_YES
        else -> {
            when {
                isAtLeastP() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                isAtLeastL() -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
    AppCompatDelegate.setDefaultNightMode(mode)
    localPreferencesRepository?.saveAppliedTheme(themeMode)
    block?.invoke(themeMode)
}

private fun isAtLeastP() = Build.VERSION.SDK_INT >= 28
private fun isAtLeastL() = Build.VERSION.SDK_INT >= 21
