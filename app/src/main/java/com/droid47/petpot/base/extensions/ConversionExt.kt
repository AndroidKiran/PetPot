package com.droid47.petpot.base.extensions

import android.content.Context
import com.droid47.petpot.search.data.models.search.FavouritePetEntity
import com.droid47.petpot.search.data.models.search.SearchPetEntity

fun Context.dp2px(dipValue: Float): Int {
    val m = resources.displayMetrics.density
    return (dipValue * m + 0.5f).toInt()
}

fun SearchPetEntity.toFavouritePet(): FavouritePetEntity =
    FavouritePetEntity().copy(
        gender = gender,
        type = type,
        photos = photos,
        colorsEntity = colorsEntity,
        breedEntity = breedEntity,
        tags = tags,
        coat = coat,
        environmentEntity = environmentEntity,
        size = size,
        species = species,
        contactEntity = contactEntity,
        name = name,
        attributesEntity = attributesEntity,
        id = id,
        publishedAt = publishedAt,
        statusModifiedAt = statusModifiedAt,
        age = age,
        status = status,
        desc = desc,
        url = url,
        distance = distance,
        videos = videos,
    ).apply {
        bookmarkStatus = this@toFavouritePet.bookmarkStatus
        bookmarkedAt = this@toFavouritePet.bookmarkedAt
    }
