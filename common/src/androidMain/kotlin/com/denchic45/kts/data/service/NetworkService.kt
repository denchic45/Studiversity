package com.denchic45.kts.data.service

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
actual class NetworkService @Inject constructor(private val context: Context) {
    actual val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
}