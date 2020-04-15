package com.droid47.petfriend.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BreedEntity(
    @field:SerializedName("secondary")
    val secondary: String? = null,

    @field:SerializedName("mixed")
    val mixed: Boolean? = null,

    @field:SerializedName("primary")
    val primary: String? = null,

    @field:SerializedName("unknown")
    val unknown: Boolean? = null
) : Parcelable