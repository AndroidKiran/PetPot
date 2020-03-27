package com.droid47.petgoogle.search.data.models.type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BreedsItemEntity(
    @field:SerializedName("name")
    val name: String? = null
) : Parcelable