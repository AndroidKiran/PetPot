package com.droid47.petgoogle.search.data.models

import android.os.Parcelable
import com.droid47.petgoogle.search.data.models.search.PetEntity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PetResponseEntity(
    @field:SerializedName("animal")
    val petEntity: PetEntity? = null
) : Parcelable