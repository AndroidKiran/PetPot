package com.droid47.petfriend.search.presentation.models

import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.search.presentation.models.PetFilterConstants.PAGE_ONE
import com.droid47.petfriend.search.presentation.models.PetFilterConstants.PAGE_SIZE
import com.droid47.petfriend.search.presentation.models.PetFilterConstants.SORT_BY_RECENT
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class PetFilters(
    @SerializedName("page")
    var page: String = PAGE_ONE.toString(),
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("breed")
    var breed: String? = null,
    @SerializedName("size")
    var size: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("age")
    var age: String? = null,
    @SerializedName("color")
    var color: String? = null,
    @SerializedName("coat")
    var coat: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("limit")
    var limit: String = PAGE_SIZE.toString(),
    @SerializedName("sort")
    var sort: String = SORT_BY_RECENT,
    @SerializedName("distance")
    var distance: String? = null
) {
    fun transformToMap(gson: Gson): Map<String, @JvmSuppressWildcards Any> =
        try {
            val jsonStr = gson.toJson(this)
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(jsonStr, mapType)
        } catch (exception: Exception) {
            CrashlyticsExt.handleException(exception)
            mapOf()
        }
}

object PetFilterConstants {
    const val PAGE_ONE = 1
    const val PAGE_SIZE = 62
    const val SORT_BY_RECENT = "recent"
    const val SORT_BY_DISTANCE = "distance"
}