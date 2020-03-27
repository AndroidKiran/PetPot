package com.droid47.petgoogle.launcher.data.entities

import android.os.Parcelable
import com.droid47.petgoogle.BuildConfig
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ClientCredentialModel(
    @field:SerializedName("grant_type")
    val grantType: String = "client_credentials",
    @field:SerializedName("client_id")
    val clientId: String = BuildConfig.CLIENT_ID,
    @field:SerializedName("client_secret")
    val clientSecret: String = BuildConfig.CLIENT_SECRET
) : Parcelable