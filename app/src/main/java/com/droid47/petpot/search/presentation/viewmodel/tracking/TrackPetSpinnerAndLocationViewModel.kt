package com.droid47.petpot.search.presentation.viewmodel.tracking

interface TrackPetSpinnerAndLocationViewModel {
    fun trackWhatPetSelected(petName: String)
    fun trackWhatLocationSelected(locationName: String)
    fun trackOnCurrentLocationSelected()
}