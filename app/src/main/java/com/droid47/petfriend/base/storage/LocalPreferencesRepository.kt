package com.droid47.petfriend.base.storage

import com.droid47.petfriend.organization.data.models.OrganizationFilter

interface LocalPreferencesRepository {

    fun saveToken(tokenStr: String)

    fun fetchToken(): String

    fun fetchAppliedTheme(): String

    fun fetchSearchLimit(): Int

    fun saveOnBoardingState()

    fun getOnBoardingState(): Boolean

    fun saveTnCState()

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
}