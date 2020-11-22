package com.droid47.petpot.home.data

import android.os.Parcelable
import com.droid47.petpot.base.storage.LocalPreferencesRepository
import com.google.android.play.core.install.model.AppUpdateType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppPrivacyPolicyEntity(
    @field:SerializedName("update_type")
    var updateType: Int = AppUpdateType.FLEXIBLE,
    @field:SerializedName("privacy_policy_version_code")
    var privacyPolicyVersionCode: Int = 0
) : Parcelable {

    fun isUpdateRequired(localPreferencesRepository: LocalPreferencesRepository) =
        privacyPolicyVersionCode > localPreferencesRepository.getPrivacyPolicyVersion()

    fun updatePreference(localPreferencesRepository: LocalPreferencesRepository) {
        localPreferencesRepository.run {
            savePrivacyPolicyVersion(privacyPolicyVersionCode)
            saveTnCState(false)
        }
    }
}