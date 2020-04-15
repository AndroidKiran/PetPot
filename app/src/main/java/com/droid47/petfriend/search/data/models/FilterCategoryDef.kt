package com.droid47.petfriend.search.data.models

import androidx.annotation.StringDef


const val COLOR = "color"
const val COAT = "coat"
const val GENDER = "gender"
const val BREED = "breed"
const val SIZE = "size"
const val AGE = "age"
const val STATUS = "status"
const val PET_TYPE = "pet_type"
const val LOCATION = "location"
const val PAGE_NUM = "page"
const val INVALID_TYPE = "invalid_type"
const val SORT = "sort"

@StringDef(
    COLOR,
    COAT,
    GENDER,
    BREED,
    SIZE,
    AGE,
    STATUS,
    PET_TYPE,
    LOCATION,
    PAGE_NUM,
    INVALID_TYPE,
    SORT
)
@Retention(AnnotationRetention.SOURCE)
annotation class FilterCategoryDef

