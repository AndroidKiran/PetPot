package com.droid47.petfriend.base.widgets.currentLocation

import android.location.Location

sealed class CurrentLocationState {
    abstract val location: Location?
}

class EnableLocationState(override val location: Location? = null) : CurrentLocationState()
class RequestLocationPermissionState(override val location: Location? = null) : CurrentLocationState()
class UpdatedLocationState(override val location: Location?) : CurrentLocationState()