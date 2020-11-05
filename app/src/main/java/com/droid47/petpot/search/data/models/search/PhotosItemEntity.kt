package com.droid47.petpot.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotosItemEntity(
    @field:SerializedName("small")
    val small: String? = null,

    @field:SerializedName("large")
    val large: String? = null,

    @field:SerializedName("medium")
    val medium: String? = null,

    @field:SerializedName("full")
    val full: String? = null
) : Parcelable {

    fun getPetFullPhoto(): String =
        when {
            !large.isNullOrEmpty() -> large
            !medium.isNullOrEmpty() -> medium
            !small.isNullOrEmpty() -> small
            else -> ""
        }

    fun getPetMediumPhoto(): String =
        when {
            !medium.isNullOrEmpty() -> medium
            !small.isNullOrEmpty() -> small
            !large.isNullOrEmpty() -> large
            else -> ""
        }

}