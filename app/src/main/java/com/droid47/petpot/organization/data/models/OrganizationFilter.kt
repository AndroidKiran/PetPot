package com.droid47.petpot.organization.data.models

import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.droid47.petpot.organization.data.models.OrganizationFilterConstants.PAGE_ONE
import com.droid47.petpot.organization.data.models.OrganizationFilterConstants.PAGE_SIZE
import com.droid47.petpot.organization.data.models.OrganizationFilterConstants.SORT_BY_NAME
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class OrganizationFilter(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("state")
    var state: String? = null,
    @SerializedName("page")
    var page: String = PAGE_ONE.toString(),
    @SerializedName("limit")
    var limit: String = PAGE_SIZE.toString(),
    @SerializedName("sort")
    var sort: String = SORT_BY_NAME
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

object OrganizationFilterConstants {
    const val PAGE_ONE = 1
    const val PAGE_SIZE = 99
    const val SORT_BY_NAME = "name"
    const val PRE_FETCH_DISTANCE = 2
    const val INITIAL_LOAD_SIZE_HINT = 1
}