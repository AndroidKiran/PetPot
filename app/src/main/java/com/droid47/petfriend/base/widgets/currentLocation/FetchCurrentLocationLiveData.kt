package com.droid47.petfriend.base.widgets.currentLocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

const val PERMISSION_ID = 47

class FetchCurrentLocationLiveData constructor(private val context: Context) :
    LiveData<CurrentLocationState>() {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 0
        fastestInterval = 0
        numUpdates = 1
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            value = UpdatedLocationState(locationResult.lastLocation)
            onInactive()
        }
    }

    override fun onActive() {
        super.onActive()
        initLocationUpdate()
    }

    override fun onInactive() {
        removeLocationUpdate()
        super.onInactive()
    }

    private fun initLocationUpdate() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {
                value = EnableLocationState()
            }
        } else {
            value = RequestLocationPermissionState()
        }
    }

    private fun removeLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkPermissions(): Boolean =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager?
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
                || locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
    }
}