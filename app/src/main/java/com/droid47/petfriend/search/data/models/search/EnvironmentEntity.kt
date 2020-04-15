package com.droid47.petfriend.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class EnvironmentEntity(
    @field:SerializedName("cats")
    val cats: Boolean? = null,

    @field:SerializedName("children")
    val children: Boolean? = null,

    @field:SerializedName("dogs")
    val dogs: Boolean? = null
) : Parcelable