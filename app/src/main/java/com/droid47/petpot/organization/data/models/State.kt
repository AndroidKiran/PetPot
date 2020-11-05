package com.droid47.petpot.organization.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class State(
    @SerializedName("name")
    val name: String,
    @SerializedName("abbreviation")
    val abbreviation: String
): Parcelable