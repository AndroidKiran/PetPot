package com.droid47.petgoogle.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class AddressEntity(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("address2")
	val address2: String? = null,

	@field:SerializedName("location")
	val city: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("postcode")
	val postcode: String? = null,

	@field:SerializedName("state")
	val state: String? = null
): Parcelable