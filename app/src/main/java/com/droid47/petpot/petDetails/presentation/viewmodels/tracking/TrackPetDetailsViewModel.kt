package com.droid47.petpot.petDetails.presentation.viewmodels.tracking

interface TrackPetDetailsViewModel {
    fun trackPetImageSwipe()
    fun trackDetailsToMap()
    fun trackFavoriteSelected(bookmarked: Boolean)
    fun trackShare()
    fun trackMail()
    fun trackCall()
    fun trackSimilarPetSelected()
}