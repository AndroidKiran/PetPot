package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LinksEntity(

    @field:SerializedName("organization")
    val organizationEntity: OrganizationEntity? = null,

    @field:SerializedName("self")
    val selfEntity: SelfEntity? = null,

    @field:SerializedName("type")
    val typeEntity: TypeEntity? = null
) : Parcelable