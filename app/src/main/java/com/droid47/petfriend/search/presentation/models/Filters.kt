package com.droid47.petfriend.search.presentation.models

import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_ONE
import com.droid47.petfriend.search.presentation.models.FilterConstants.PAGE_SIZE
import com.droid47.petfriend.search.presentation.models.FilterConstants.SORT_BY_RECENT

data class Filters(

    var page: String = PAGE_ONE.toString(),

    var type: String? = null,

    var breed: String? = null,

    var size: String? = null,

    var gender: String? = null,

    var age: String? = null,

    var color: String? = null,

    var coat: String? = null,

    var status: String? = null,

    var location: String? = null,

    var limit: String = PAGE_SIZE.toString(),

    var sort: String = SORT_BY_RECENT,

    var distance: String? = null
)

object FilterConstants{
    const val PAGE_ONE = 1
    const val PAGE_SIZE = 31
    const val SORT_BY_RECENT = "recent"
    const val SORT_BY_DISTANCE = "distance"
}