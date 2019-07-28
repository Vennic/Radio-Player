package com.kuzheevadel.vmplayerv2.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectivityHelper {
    companion object {
        fun isConnectedToNetwork(context: Context?): Boolean {
            var isConnected = false

            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (cm.activeNetworkInfo != null) {
                val activeNetwork: NetworkInfo = cm.activeNetworkInfo
                isConnected = activeNetwork.isConnected
            }

            return isConnected
        }
    }
}