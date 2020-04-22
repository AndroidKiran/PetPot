package com.droid47.petfriend.home.data

import android.os.Parcelable
import com.google.android.play.core.install.model.AppUpdateType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppUpgradeEntity(
    @SerializedName("update_type")
    var updateType: Int = AppUpdateType.FLEXIBLE,
    @SerializedName("play_store_version_code")
    var currentPlayStoreVersionCode: Int = 0
) : Parcelable