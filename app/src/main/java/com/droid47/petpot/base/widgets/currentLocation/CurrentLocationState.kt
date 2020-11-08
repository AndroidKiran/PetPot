package com.droid47.petpot.base.widgets.currentLocation

import android.location.Location

sealed class CurrentLocationState {
    abstract val location: Location?
}

data class EnableLocationState(override val location: Location? = null) : CurrentLocationState()
data class RequestLocationPermissionState(override val location: Location? = null) : CurrentLocationState()
data class UpdatedLocationState(override val location: Location?) : CurrentLocationState()