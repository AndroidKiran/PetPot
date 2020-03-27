package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class AttributesEntity(

    @field:SerializedName("shots_current")
    val shotsCurrent: Boolean? = null,

    @field:SerializedName("special_needs")
    val specialNeeds: Boolean? = null,

    @field:SerializedName("declawed")
    val declawed: Boolean? = null,

    @field:SerializedName("spayed_neutered")
    val spayedNeutered: Boolean? = null,

    @field:SerializedName("house_trained")
    val houseTrained: Boolean? = null
) : Parcelable