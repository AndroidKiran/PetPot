package com.droid47.petpot.base.extensions

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import com.droid47.petpot.base.firebase.CrashlyticsExt
import com.google.android.gms.maps.model.LatLng
import java.io.InputStream
import java.util.*

fun Context.isNetworkAvailable(): Boolean =
    try {
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .isActiveNetworkMetered
    } catch (e: Exception) {
        CrashlyticsExt.handleException(e)
        false
    }

fun String.applyBracketFilter(): String =
    substring(1, length - 1).replace(", $", "")

fun Context.getAddressFromLocation(location: Location): String? {
    return try {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1) ?: emptyList()
        if (addresses.isEmpty()) return null
        val city = addresses[0].locality ?: ""
        val state = addresses[0].adminArea ?: ""
        val zip = addresses[0].postalCode ?: ""
        val country = addresses[0].countryName ?: ""
        when {
            city.isNotEmpty() -> city
            state.isNotEmpty() -> state
            zip.isNotEmpty() -> zip
            country.isNotEmpty() -> country
            else -> null
        }
    } catch (exception: Exception) {
        CrashlyticsExt.handleException(exception)
        null
    }

}

fun Context.getLocationFromAddress(address: String): LatLng {
    return try {
        val addressList = Geocoder(this).getFromLocationName(address, 5)
        addressList?.takeIf { it.isNotEmpty() }?.let {
            val location = it[0]
            LatLng(location.latitude, location.longitude)
        } ?: run {
            LatLng(0.0, 0.0)
        }
    } catch (exception: Exception) {
        CrashlyticsExt.handleException(exception)
        LatLng(0.0, 0.0)
    }
}

fun Context.readJSONFromAsset(fileName: String): String? {
    return try {
        val inputStream: InputStream = assets.open(fileName)
        inputStream.bufferedReader().use { it.readText() }
    } catch (ex: Exception) {
        return null
    }
}