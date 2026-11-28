package com.kangurusiaga.app.data.remote.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kangurusiaga.app.core.common.AppLogger
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mengelola Firebase Remote Config untuk fitur verifikasi foto posisi PMK.
 *
 * Key: `pmk_photo_verification` (Number, default lokal = 1)
 * - 1: Pop-up hanya muncul sekali saja (saat pertama kali PMK)
 * - 2: Pop-up hanya muncul sekali sehari (saat pertama kali PMK di hari tersebut)
 * - 3: Pop-up muncul setiap kali mau mulai PMK
 */
@Singleton
class PmkRemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    companion object {
        private const val TAG = "PmkRemoteConfig"
        const val KEY_PMK_PHOTO_VERIFICATION = "pmk_photo_verification"
        const val MODE_ONCE_EVER = 1
        const val MODE_ONCE_PER_DAY = 2
        const val MODE_EVERY_TIME = 3
        const val DEFAULT_MODE = MODE_ONCE_EVER

        fun shouldShowVerificationPopup(
            mode: Int,
            hasSeenOnce: Boolean,
            lastVerifiedDateKey: String?,
            todayDateKey: String
        ): Boolean {
            return when (mode) {
                MODE_ONCE_PER_DAY -> lastVerifiedDateKey != todayDateKey
                MODE_EVERY_TIME -> true
                else -> !hasSeenOnce
            }
        }

        fun todayDateKey(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }
    }

    suspend fun fetchAndActivate(): Boolean {
        return try {
            val activated = remoteConfig.fetchAndActivate().await()
            AppLogger.d(
                TAG,
                "Remote Config fetchAndActivate=$activated, $KEY_PMK_PHOTO_VERIFICATION=${getPmkPhotoVerificationMode()}"
            )
            activated
        } catch (e: Exception) {
            AppLogger.w(TAG, "Gagal fetch Remote Config, gunakan nilai default/cache: ${e.message}")
            false
        }
    }

    fun getPmkPhotoVerificationMode(): Int {
        return try {
            val raw = remoteConfig.getLong(KEY_PMK_PHOTO_VERIFICATION).toInt()
            if (raw in MODE_ONCE_EVER..MODE_EVERY_TIME) raw else DEFAULT_MODE
        } catch (_: Exception) {
            DEFAULT_MODE
        }
    }
}
