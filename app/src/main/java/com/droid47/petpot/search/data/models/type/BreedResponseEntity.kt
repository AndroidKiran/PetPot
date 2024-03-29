package com.droid47.petpot.search.data.models.type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BreedResponseEntity(
    @field:SerializedName("breeds")
    val breeds: List<BreedsItemEntity>? = null
) : Parcelable