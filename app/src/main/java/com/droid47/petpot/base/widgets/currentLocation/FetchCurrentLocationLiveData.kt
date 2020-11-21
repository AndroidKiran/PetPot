package com.droid47.petpot.base.widgets.currentLocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData

const val PERMISSION_ID = 47
private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1f
private const val MIN_TIME_BW_UPDATES: Long = 1L

class FetchCurrentLocationLiveData constructor(private val context: Context) :
    LiveData<CurrentLocationState>(), LocationListener {

    private val locationManager: LocationManager? by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    private val isGpsProviderEnabled =
        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false

    private val isNetworkProviderEnabled =
        locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

    override fun onActive() {
        super.onActive()
        initLocationUpdate()
    }

    override fun onInactive() {
        removeLocationUpdate()
        super.onInactive()
    }

    override fun onLocationChanged(location: Location) {
        value = UpdatedLocationState(location)
        onInactive()
    }

    private fun onLocationUpdated(location: Location?) {
        location?.run {
            onLocationChanged(this)
        } ?: run {
            onInactive()
        }
    }

    private fun initLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                locationManager?.requestLocationUpdates(
                    getProvider(),
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this@FetchCurrentLocationLiveData
                )
                val location = locationManager?.getLastKnownLocation(getProvider())
                onLocationUpdated(location)
            } else {
                value = EnableLocationState()
            }
        } else {
            value = RequestLocationPermissionState()
        }
    }

    private fun removeLocationUpdate() {
        locationManager?.removeUpdates(this@FetchCurrentLocationLiveData)
    }

    private fun isLocationEnabled(): Boolean {
        return isGpsProviderEnabled || isNetworkProviderEnabled
    }

    private fun getProvider(): String = when {
        isNetworkProviderEnabled -> LocationManager.NETWORK_PROVIDER
        else -> LocationManager.GPS_PROVIDER
    }
}