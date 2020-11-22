package com.droid47.petpot.home.data

import android.os.Parcelable
import com.droid47.petpot.BuildConfig
import com.google.android.play.core.install.model.AppUpdateType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppUpgradeEntity(
    @field:SerializedName("update_type")
    var updateType: Int = AppUpdateType.FLEXIBLE,
    @field:SerializedName("play_store_version_code")
    var currentPlayStoreVersionCode: Int = 0
) : Parcelable {

    fun isUpdateRequired() = currentPlayStoreVersionCode > BuildConfig.VERSION_CODE
}