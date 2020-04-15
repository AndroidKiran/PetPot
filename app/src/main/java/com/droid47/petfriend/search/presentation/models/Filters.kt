package com.droid47.petfriend.search.presentation.models

data class Filters(

    var page: String = "1",

    var type: String? = null,

    var breed: String? = null,

    var size: String? = null,

    var gender: String? = null,

    var age: String? = null,

    var color: String? = null,

    var coat: String? = null,

    var status: String? = null,

    var location: String? = null,

    var limit: String = "20",

    var sort: String = "recent",

    var distance: String? = null
)