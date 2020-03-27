package com.droid47.petgoogle.launcher.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TokenModel(
    @field:SerializedName("token_type")
    val tokenType: String,
    @field:SerializedName("expires_in")
    val expiresIn: Long,
    @field:SerializedName("access_token")
    val accessToken: String
) : Parcelable