package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class TypeEntity(
	@field:SerializedName("href")
	val href: String? = null
): Parcelable