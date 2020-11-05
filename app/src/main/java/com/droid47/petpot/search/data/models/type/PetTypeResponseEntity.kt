package com.droid47.petpot.search.data.models.type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PetTypeResponseEntity(
    @field:SerializedName("types")
    val typeEntities: List<PetTypeEntity>? = null
) : Parcelable