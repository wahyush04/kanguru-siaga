package com.kangurusiaga.app.core.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkMonitor {
    fun isOnline(): Boolean
}

@Singleton
class ConnectivityNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {

    companion object {
        private const val TAG = "NetworkMonitor"
    }

    override fun isOnline(): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connectivityManager == null) {
                AppLogger.w(TAG, "ConnectivityManager is null")
                return false
            }

            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork == null) {
                AppLogger.d(TAG, "activeNetwork is null (tidak ada jaringan aktif)")
                return false
            }

            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (capabilities == null) {
                AppLogger.d(TAG, "NetworkCapabilities is null")
                return false
            }

            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            AppLogger.d(TAG, "Deteksi konektivitas internet: hasInternet=$hasInternet")
            hasInternet
        } catch (e: Exception) {
            AppLogger.e(TAG, "Gagal memeriksa status koneksi jaringan: ${e.message}", e)
            false
        }
    }
}
