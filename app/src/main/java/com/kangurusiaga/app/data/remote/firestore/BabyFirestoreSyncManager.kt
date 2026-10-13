package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.kangurusiaga.app.BuildConfig
import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.core.common.NetworkMonitor
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface BabyFirestoreSyncManager {
    /**
     * Menyinkronkan identitas data bayi ke Firebase Firestore saat onboarding.
     * Bersifat non-blocking, hanya aktif saat online, dan aman terhadap exception.
     */
    suspend fun syncOnboardingBaby(baby: Baby, currentWeightGram: Int): Boolean
}

@Singleton
class BabyFirestoreSyncManagerImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BabyFirestoreSyncManager {

    companion object {
        const val TAG = "BabyFirestoreSync"
        const val COLLECTION_BABY_PROFILES = "baby_onboarding_profiles"
    }

    override suspend fun syncOnboardingBaby(baby: Baby, currentWeightGram: Int): Boolean {
        return withContext(ioDispatcher) {
            // 1. Cek koneksi internet. Jika offline, lewati tanpa blocking atau error.
            val isOnline = networkMonitor.isOnline()
            AppLogger.d(TAG, "Mengecek konektivitas sebelum sync Firestore: isOnline=$isOnline")
            if (!isOnline) {
                AppLogger.w(TAG, "Perangkat offline: Melewati sinkronisasi onboarding bayi (${baby.name}) ke Firestore")
                CrashlyticsLogger.log("Offline: Melewati sinkronisasi onboarding bayi (${baby.name}) ke Firestore")
                return@withContext false
            }

            // 2. Siapkan DTO sesuai ketentuan 6 field utama
            val genderString = when (baby.gender) {
                Gender.MALE -> "LAKI_LAKI"
                Gender.FEMALE -> "PEREMPUAN"
                Gender.UNSPECIFIED, null -> "TIDAK_DIKETAHUI"
            }

            val dto = BabyOnboardingFirestoreDto.create(
                babyName = baby.name,
                birthDateEpochMillis = baby.birthDateEpochMillis,
                gestationalAgeWeeks = baby.gestationalAgeWeeks,
                birthWeightGram = baby.birthWeightGram,
                currentWeightGram = currentWeightGram,
                gender = genderString,
                environment = BuildConfig.ENVIRONMENT,
                appVersion = BuildConfig.VERSION_NAME
            )

            // 3. Eksekusi pengiriman ke Firestore
            try {
                AppLogger.d(TAG, "Mengirim profil bayi '${baby.name}' ke koleksi '$COLLECTION_BABY_PROFILES' di Firestore: ${dto.toMap()}")
                val documentReference = firestore.collection(COLLECTION_BABY_PROFILES)
                    .add(dto.toMap())
                    .await()

                AppLogger.i(TAG, "BERHASIL mencatat profil onboarding bayi ke Firestore dengan ID: ${documentReference.id}")
                CrashlyticsLogger.log("Berhasil mencatat profil onboarding bayi ke Firestore: id=${documentReference.id}")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan onboarding bayi ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                CrashlyticsLogger.log("Gagal menyinkronkan onboarding bayi ke Firestore: ${e.localizedMessage}")
                false
            }
        }
    }
}
