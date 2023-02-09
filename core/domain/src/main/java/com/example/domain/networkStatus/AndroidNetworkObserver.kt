package com.example.domain.networkStatus

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

fun getAndroidNetworkObserver(context: Context) = NetworkObserver {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                launch { send(NetworkStatus.CONNECTED) }
            }
            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                launch { send(NetworkStatus.DISCONNECTED) }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        }
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}