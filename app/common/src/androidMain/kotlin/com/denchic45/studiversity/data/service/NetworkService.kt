package com.denchic45.studiversity.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
actual class NetworkService(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    actual val isNetworkAvailable: Boolean
        get() {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


    actual fun observeNetwork(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(true) }
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(false) }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                launch { send(false) }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                launch { send(false) }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }.distinctUntilChanged()
}