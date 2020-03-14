package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.droid47.petgoogle.base.extensions.isNotEmpty
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
            full?.isNotEmpty() == true -> full
            large?.isNotEmpty() == true -> large
            medium?.isNotEmpty() == true -> medium
            small?.isNotEmpty() == true -> small
            else -> ""
        }

}