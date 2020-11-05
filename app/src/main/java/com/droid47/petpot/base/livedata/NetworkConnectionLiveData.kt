package com.droid47.petpot.base.livedata

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkConnectionLiveData constructor(context: Context) :
    LiveData<NetworkConnectionLiveData.NetworkState>() {

    private var hasNetworkChanged: Boolean = false
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun networkRequestBuilder(): NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkStateObject = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            hasNetworkChanged = true;
            postValue(NetworkState.ConnectionLost)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            hasNetworkChanged = true
            postValue(NetworkState.DisConnected)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (hasNetworkChanged) {
                hasNetworkChanged = false
                postValue(NetworkState.Connected)
            }
        }
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerNetworkCallback(networkRequestBuilder(), networkStateObject)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkStateObject)
    }

    sealed class NetworkState {
        object Connected : NetworkState()
        object DisConnected : NetworkState()
        object ConnectionLost : NetworkState()
    }
}