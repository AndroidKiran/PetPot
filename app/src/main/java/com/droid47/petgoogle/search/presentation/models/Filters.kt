package com.droid47.petgoogle.search.presentation.models

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
) {

//    fun toMap(): Map<String, @JvmSuppressWildcards Any> {
//        return mutableMapOf<String, @JvmSuppressWildcards Any>().apply {
//            this["limit"] = 40
//            this["page"] = this@Filters.page
//            this@Filters.type?.takeIf { it.isNotEmpty() }?.let {
//                this["type"] = it
//            }
//            this@Filters.breed?.takeIf { it.isNotEmpty() }?.let {
//                this["breed"] = it
//            }
//            this@Filters.size?.takeIf { it.isNotEmpty() }?.let {
//                this["size"] = it
//            }
//            this@Filters.gender?.takeIf { it.isNotEmpty() }?.let {
//                this["gender"] = it
//            }
//            this@Filters.coat?.takeIf { it.isNotEmpty() }?.let {
//                this["coat"] = it
//            }
//            this@Filters.color?.takeIf { it.isNotEmpty() }?.let {
//                this["color"] = it
//            }
//            this@Filters.status?.takeIf { it.isNotEmpty() }?.let {
//                this["status"] = it
//            }
//            this@Filters.age?.takeIf { it.isNotEmpty() }?.let {
//                this["age"] = it
//            }
//            this@Filters.location?.takeIf { it.isNotEmpty() }?.let {
//                this["location"] = it
//            }
//        }
//    }
}