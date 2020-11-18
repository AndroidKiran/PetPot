package com.droid47.petpot.search.data.models.type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BreedsItemEntity(
    @field:SerializedName("name")
    val name: String? = null
) : Parcelable