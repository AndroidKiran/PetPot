package com.droid47.petgoogle.search.data.models.search


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InvalidParamEntity(
    @SerializedName("in")
    var inX: String? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("path")
    var path: String? = null
): Parcelable