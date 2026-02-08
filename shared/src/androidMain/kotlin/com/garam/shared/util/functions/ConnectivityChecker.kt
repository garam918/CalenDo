package com.garam.shared.util.functions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

actual object ConnectivityChecker {

    private lateinit var appContext: Context


    fun init(context: Context) {

        appContext = context.applicationContext
    }

    actual val isOnline: Boolean
        get() {
            val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
}