package com.droid47.petgoogle.app.data

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.droid47.petgoogle.R
import com.droid47.petgoogle.app.domain.repositories.LocalPreferencesRepository
import com.droid47.petgoogle.base.extensions.DEFAULT_MODE
import javax.inject.Inject

class LocalPreferenceDataSource @Inject constructor(private val application: Application) :
    LocalPreferencesRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(application)
    }

    override fun saveToken(tokenStr: String) {
        sharedPreferences.edit {
            putString(KEY_TOKEN, tokenStr).apply()
        }
    }

    override fun fetchToken(): String = sharedPreferences.getString(KEY_TOKEN, "") ?: ""

    override fun fetchAppliedTheme(): String =
        sharedPreferences.getString(application.getString(R.string.key_current_theme), DEFAULT_MODE)
            ?: DEFAULT_MODE

    override fun fetchSearchLimit(): Int =
        sharedPreferences.getInt(application.getString(R.string.key_search_limit), 20)

    override fun saveOnBoardingState() {
        sharedPreferences.edit {
            putBoolean(KEY_ON_BOARDING_STATE, true)
        }
    }

    override fun getOnBoardingState(): Boolean =
        sharedPreferences.getBoolean(KEY_ON_BOARDING_STATE, false)

    override fun saveTnCState() {
        sharedPreferences.edit {
            putBoolean(KEY_TNC_STATE, true)
        }
    }

    override fun getTnCState(): Boolean =
        sharedPreferences.getBoolean(KEY_TNC_STATE, false)

    override fun saveFcmToken(token: String) {
        sharedPreferences.edit {
            putString(KEY_FCM_TOKEN, token)
        }
    }

    override fun getFcmToken(): String? =
        sharedPreferences.getString(KEY_FCM_TOKEN, null)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_ON_BOARDING_STATE = "on_boarding_state"
        const val KEY_TNC_STATE = "tnc_state"
        const val KEY_FCM_TOKEN = "fcm_token"
    }
}