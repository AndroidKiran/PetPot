package com.droid47.petgoogle.search.data.models.search


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ErrorEntity(
    @SerializedName("detail")
    var detail: String? = null,
    @SerializedName("invalid-params")
    var invalidParams: List<InvalidParamEntity>? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("type")
    var type: String? = null
) : Parcelable