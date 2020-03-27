package com.droid47.petgoogle.home.data

import android.os.Parcelable
import com.google.android.play.core.install.model.AppUpdateType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppUpgradeEntity(
    @SerializedName("play_store_version_code")
    var currentPlayStoreVersionCode: Int = 0,
    @SerializedName("upgrade_info_list")
    var upgradeInfoList: List<UpgradeInfoEntity>? = null
) : Parcelable

@Parcelize
data class UpgradeInfoEntity(
    @SerializedName("update_type")
    var updateType: Int = AppUpdateType.FLEXIBLE,
    @SerializedName("app_version_code")
    var appVersionCode: Int = 0
) : Parcelable