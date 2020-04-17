package com.droid47.petfriend.base.extensions

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import java.util.*


fun Context.isNetworkAvailable(): Boolean =
    try {
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .isActiveNetworkMetered
    } catch (e: Exception) {
        CrashlyticsExt.logHandledException(e)
        false
    }


fun String.applyBracketFilter(): String =
    substring(1, length - 1).replace(", $", "")

fun Context.getLocationAddress(location: Location): String? {
    val geoCoder = Geocoder(this, Locale.getDefault())
    val addresses: List<Address> =
        geoCoder.getFromLocation(location.latitude, location.longitude, 1) ?: emptyList()
    if (addresses.isEmpty()) return null
    val city = addresses[0].locality ?: ""
    val state = addresses[0].adminArea ?: ""
    val zip = addresses[0].postalCode ?: ""
    val country = addresses[0].countryName ?: ""
    return when {
        city.isNotEmpty() -> city
        state.isNotEmpty() -> state
        zip.isNotEmpty() -> zip
        country.isNotEmpty() -> country
        else -> null
    }
}