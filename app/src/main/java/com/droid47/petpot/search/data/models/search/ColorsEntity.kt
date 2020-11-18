package com.droid47.petpot.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorsEntity(
    @field:SerializedName("secondary")
    val secondary: String? = null,

    @field:SerializedName("tertiary")
    val tertiary: String? = null,

    @field:SerializedName("primary")
    val primary: String? = null
) : Parcelable