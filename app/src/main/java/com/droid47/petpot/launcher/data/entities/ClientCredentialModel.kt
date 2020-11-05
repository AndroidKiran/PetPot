package com.droid47.petpot.launcher.data.entities

import android.os.Parcelable
import com.droid47.petpot.app.AppKeyMaker
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ClientCredentialModel(
    @field:SerializedName("grant_type")
    val grantType: String = "client_credentials",
    @field:SerializedName("client_id")
    val clientId: String = AppKeyMaker.getClientIdValue(),
    @field:SerializedName("client_secret")
    val clientSecret: String = AppKeyMaker.getClientSecretValue()
) : Parcelable