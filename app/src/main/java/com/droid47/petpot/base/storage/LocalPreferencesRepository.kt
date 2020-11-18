package com.droid47.petpot.base.storage

import android.content.SharedPreferences
import com.droid47.petpot.organization.data.models.OrganizationFilter

interface LocalPreferencesRepository {

    val sharedPreferences: SharedPreferences

    fun fetchAppliedTheme(): String

    fun saveAppliedTheme(theme: String)

    fun fetchSearchLimit(): Int

    fun saveOnBoardingState()

    fun getOnBoardingState(): Boolean

    fun saveTnCState(status: Boolean)

    fun getTnCState(): Boolean

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    fun saveSelectPetPosition(position: Int)

    fun getSelectedPetPosition(): Int

    fun saveSelectedPet(petName: String)

    fun getSelectedPet(): String?

    fun saveLocation(location: String)

    fun getLocation(): String?

    fun saveOrganizationFilter(organizationFilter: OrganizationFilter)

    fun getOrganizationFilter(): OrganizationFilter

    fun getNotificationStatus(): Boolean

    fun savePrivacyPolicyVersion(version: Int)

    fun getPrivacyPolicyVersion(): Int
}