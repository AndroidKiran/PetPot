package com.droid47.petpot.search.data.models.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PaginationEntity(

    @field:SerializedName("count_per_page")
    val countPerPage: Int = 0,

    @field:SerializedName("total_count")
    val totalCount: Int = 0,

    @field:SerializedName("total_pages")
    val totalPages: Int = 0,

    @field:SerializedName("current_page")
    var currentPage: Int = 0
) : Parcelable