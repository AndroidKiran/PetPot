package com.droid47.petpot.app.data

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.DEFAULT_MODE
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.droid47.petpot.organization.data.models.OrganizationFilter
import com.google.gson.Gson
import javax.inject.Inject

class LocalPreferenceDataSource @Inject constructor(
    private val application: Application,
    private val gson: Gson
) : LocalPreferencesRepository {

    override val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(application)
    }

    override fun fetchAppliedTheme(): String =
        sharedPreferences.getString(application.getString(R.string.key_current_theme), DEFAULT_MODE)
            ?: DEFAULT_MODE

    override fun saveAppliedTheme(theme: String) {
        sharedPreferences.edit {
            putString(application.getString(R.string.key_current_theme), theme)
        }
    }

    override fun fetchSearchLimit(): Int =
        sharedPreferences.getInt(application.getString(R.string.key_search_limit), 20)

    override fun saveOnBoardingState() {
        sharedPreferences.edit {
            putBoolean(KEY_ON_BOARDING_STATE, true)
        }
    }

    override fun getOnBoardingState(): Boolean =
        sharedPreferences.getBoolean(KEY_ON_BOARDING_STATE, false)

    override fun saveTnCState(status: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_TNC_STATE, status)
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

    override fun saveSelectPetPosition(position: Int) {
        sharedPreferences.edit {
            putInt(KEY_SELECTED_PET_POSITION, position)
        }
    }

    override fun getSelectedPetPosition(): Int =
        sharedPreferences.getInt(KEY_SELECTED_PET_POSITION, 0)

    override fun saveSelectedPet(petName: String) {
        sharedPreferences.edit {
            putString(KEY_SELECTED_PET, petName)
        }
    }

    override fun getSelectedPet(): String? =
        sharedPreferences.getString(KEY_SELECTED_PET, "")

    override fun saveLocation(location: String) {
        sharedPreferences.edit {
            putString(KEY_LOCATION, location)
        }
    }

    override fun getLocation(): String? =
        sharedPreferences.getString(KEY_LOCATION, "")

    override fun saveOrganizationFilter(organizationFilter: OrganizationFilter) {
        sharedPreferences.edit {
            putString(
                KEY_ORGANIZATION_FILTER, try {
                    gson.toJson(organizationFilter)
                } catch (exception: Exception) {
                    CrashlyticsExt.handleException(exception)
                    ""
                }
            )
        }
    }

    override fun getOrganizationFilter(): OrganizationFilter {
        return try {
            val prefStr = sharedPreferences.getString(KEY_ORGANIZATION_FILTER, "")
            gson.fromJson(prefStr, OrganizationFilter::class.java)
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
            OrganizationFilter()
        }
    }

    override fun getNotificationStatus(): Boolean =
        sharedPreferences.getBoolean(application.getString(R.string.key_notification_enabled), true)

    override fun savePrivacyPolicyVersion(version: Int) {
        sharedPreferences.edit {
            putInt(KEY_PRIVACY_POLICY, version)
        }
    }

    override fun getPrivacyPolicyVersion(): Int =
        sharedPreferences.getInt(KEY_PRIVACY_POLICY, 1)

    companion object {
        const val KEY_ON_BOARDING_STATE = "on_boarding_state"
        const val KEY_TNC_STATE = "tnc_state"
        const val KEY_FCM_TOKEN = "fcm_token"
        const val KEY_SELECTED_PET_POSITION = "selected_pet_position"
        const val KEY_SELECTED_PET = "selected_pet"
        const val KEY_LOCATION = "location"
        const val KEY_ORGANIZATION_FILTER = "organization_filter"
        const val KEY_PRIVACY_POLICY = "privacy_policy"
    }
}