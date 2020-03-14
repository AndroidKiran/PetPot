package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SearchResponseEntity(
    @field:SerializedName("pagination")
	var paginationEntity: PaginationEntity? = null,

    @field:SerializedName("animals")
	var animals: List<PetEntity>? = null
): Parcelable