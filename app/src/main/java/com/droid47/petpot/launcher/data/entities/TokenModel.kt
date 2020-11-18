package com.droid47.petpot.launcher.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TokenModel(
    @field:SerializedName("token_type")
    val tokenType: String? = null,
    @field:SerializedName("expires_in")
    val expiresIn: Long = 3600L,
    @field:SerializedName("access_token")
    val accessToken: String? = null
) : Parcelable