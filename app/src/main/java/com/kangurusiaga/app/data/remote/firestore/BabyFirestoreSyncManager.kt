package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.kangurusiaga.app.BuildConfig
import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.core.common.NetworkMonitor
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.dao.PrematureScreeningDao
import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.data.local.entity.PrematureScreeningEntity
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface BabyFirestoreSyncManager {
    /**
     * Menyinkronkan identitas data bayi ke Firebase Firestore saat onboarding.
     * Kompatibel dengan skema lama (baby_onboarding_profiles) dan skema baru (baby_records).
     */
    suspend fun syncOnboardingBaby(
        baby: Baby,
        currentWeightGram: Int,
        currentLengthCm: Float = baby.birthLengthCm,
        currentHeadCircumferenceCm: Float = baby.birthHeadCircumferenceCm
    ): Boolean

    /**
     * Menyinkronkan profil lengkap Ibu dan Bayi ke koleksi baby_records/{babyDocId}.
     */
    suspend fun syncBabyProfile(
        baby: Baby,
        currentWeightGram: Int? = null,
        currentLengthCm: Float? = null,
        currentHeadCircumferenceCm: Float? = null
    ): Boolean

    /**
     * Menyinkronkan satu sesi PMK ke subkoleksi pmk_sessions.
     */
    suspend fun syncPmkSession(session: PmkSessionEntity): Boolean

    /**
     * Menyinkronkan seluruh riwayat sesi PMK lokal untuk bayi tertentu.
     */
    suspend fun syncAllPmkSessions(babyId: Long): Boolean

    /**
     * Menyinkronkan satu entitas skrining (jadwal/hasil) ke subkoleksi screenings.
     */
    suspend fun syncScreening(screening: PrematureScreeningEntity): Boolean

    /**
     * Menyinkronkan seluruh data skrining untuk bayi tertentu.
     */
    suspend fun syncAllScreenings(babyId: Long): Boolean

    /**
     * Menyinkronkan satu data pengukuran pertumbuhan ke subkoleksi growth_measurements.
     */
    suspend fun syncGrowthMeasurement(measurement: GrowthMeasurementEntity): Boolean

    /**
     * Menyinkronkan seluruh data pengukuran pertumbuhan untuk bayi tertentu.
     */
    suspend fun syncAllGrowthMeasurements(babyId: Long): Boolean

    /**
     * Menyinkronkan seluruh data (profil ibu & bayi, riwayat PMK, skrining, dan pertumbuhan).
     */
    suspend fun syncAll(babyId: Long? = null): Boolean
}

@Singleton
class BabyFirestoreSyncManagerImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val babyDao: BabyDao,
    private val pmkSessionDao: PmkSessionDao,
    private val prematureScreeningDao: PrematureScreeningDao,
    private val growthMeasurementDao: GrowthMeasurementDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BabyFirestoreSyncManager {

    companion object {
        const val TAG = "BabyFirestoreSync"
        const val COLLECTION_BABY_PROFILES = "baby_onboarding_profiles"
        const val COLLECTION_BABY_RECORDS = "baby_records"
        const val SUBCOLLECTION_PMK = "pmk_sessions"
        const val SUBCOLLECTION_SCREENINGS = "screenings"
        const val SUBCOLLECTION_GROWTH = "growth_measurements"
    }

    private suspend fun ensureParentBabyDocExists(babyDocId: String, babyId: Long) {
        val docRef = firestore.collection(COLLECTION_BABY_RECORDS).document(babyDocId)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            val babyEntity = babyDao.getActiveBabyOnce()
            if (babyEntity != null) {
                val baby = babyEntity.toDomain()
                val genderString = when (baby.gender) {
                    Gender.MALE -> "LAKI_LAKI"
                    Gender.FEMALE -> "PEREMPUAN"
                    Gender.UNSPECIFIED, null -> "TIDAK_DIKETAHUI"
                }
                val dto = BabyRecordFirestoreDto.create(
                    babyIdLocal = baby.id,
                    motherName = baby.motherName,
                    motherPhoneNumber = baby.motherPhoneNumber,
                    babyName = baby.name,
                    birthDateEpochMillis = baby.birthDateEpochMillis,
                    gestationalAgeWeeks = baby.gestationalAgeWeeks,
                    birthWeightGram = baby.birthWeightGram,
                    currentWeightGram = baby.birthWeightGram,
                    birthLengthCm = baby.birthLengthCm,
                    currentLengthCm = baby.birthLengthCm,
                    birthHeadCircumferenceCm = baby.birthHeadCircumferenceCm,
                    currentHeadCircumferenceCm = baby.birthHeadCircumferenceCm,
                    gender = genderString,
                    environment = BuildConfig.ENVIRONMENT,
                    appVersion = BuildConfig.VERSION_NAME
                )
                docRef.set(dto.toMap(isNewDocument = true)).await()
                AppLogger.i(TAG, "Parent doc baby_records/$babyDocId diinisialisasi otomatis")
            }
        }
    }

    override suspend fun syncOnboardingBaby(
        baby: Baby,
        currentWeightGram: Int,
        currentLengthCm: Float,
        currentHeadCircumferenceCm: Float
    ): Boolean {
        return withContext(ioDispatcher) {
            val isOnline = networkMonitor.isOnline()
            AppLogger.d(TAG, "Mengecek konektivitas sebelum sync Firestore: isOnline=$isOnline")
            if (!isOnline) {
                AppLogger.w(TAG, "Perangkat offline: Melewati sinkronisasi onboarding bayi (babyId=${baby.id}) ke Firestore")
                CrashlyticsLogger.log("Offline: Melewati sinkronisasi onboarding bayi ke Firestore")
                return@withContext false
            }

            try {
                // 1. Sinkronkan profil utama ibu & bayi ke baby_records/{babyDocId}
                val profileSuccess = syncBabyProfile(
                    baby = baby,
                    currentWeightGram = currentWeightGram,
                    currentLengthCm = currentLengthCm,
                    currentHeadCircumferenceCm = currentHeadCircumferenceCm
                )

                if (profileSuccess) {
                    // 2. Sinkronkan data awal skrining dan pengukuran pertumbuhan saat onboarding
                    syncAllScreenings(baby.id)
                    syncAllGrowthMeasurements(baby.id)
                    AppLogger.i(TAG, "BERHASIL menyinkronkan profil, skrining, dan pertumbuhan onboarding ke baby_records")
                    true
                } else {
                    AppLogger.w(TAG, "Gagal menyinkronkan profil bayi saat onboarding")
                    false
                }
            } catch (e: Exception) {
                AppLogger.e(TAG, "Gagal sinkron onboarding ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncBabyProfile(
        baby: Baby,
        currentWeightGram: Int?,
        currentLengthCm: Float?,
        currentHeadCircumferenceCm: Float?
    ): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) {
                AppLogger.d(TAG, "Offline: Melewati sinkronisasi profil bayi (babyId=${baby.id}) ke Firestore")
                return@withContext false
            }

            try {
                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                val docRef = firestore.collection(COLLECTION_BABY_RECORDS).document(babyDocId)

                val genderString = when (baby.gender) {
                    Gender.MALE -> "LAKI_LAKI"
                    Gender.FEMALE -> "PEREMPUAN"
                    Gender.UNSPECIFIED, null -> "TIDAK_DIKETAHUI"
                }

                val dto = BabyRecordFirestoreDto.create(
                    babyIdLocal = baby.id,
                    motherName = baby.motherName,
                    motherPhoneNumber = baby.motherPhoneNumber,
                    babyName = baby.name,
                    birthDateEpochMillis = baby.birthDateEpochMillis,
                    gestationalAgeWeeks = baby.gestationalAgeWeeks,
                    birthWeightGram = baby.birthWeightGram,
                    currentWeightGram = currentWeightGram ?: baby.birthWeightGram,
                    birthLengthCm = baby.birthLengthCm,
                    currentLengthCm = currentLengthCm ?: baby.birthLengthCm,
                    birthHeadCircumferenceCm = baby.birthHeadCircumferenceCm,
                    currentHeadCircumferenceCm = currentHeadCircumferenceCm ?: baby.birthHeadCircumferenceCm,
                    gender = genderString,
                    environment = BuildConfig.ENVIRONMENT,
                    appVersion = BuildConfig.VERSION_NAME
                )

                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    val existingCreatedAt = snapshot.get("createdAtServer")
                    val map = dto.toMap(isNewDocument = false).toMutableMap()
                    if (existingCreatedAt != null) {
                        map["createdAtServer"] = existingCreatedAt
                    }
                    docRef.set(map).await()
                    AppLogger.i(TAG, "BERHASIL update profil bayi di Firestore: babyDocId=$babyDocId")
                } else {
                    docRef.set(dto.toMap(isNewDocument = true)).await()
                    AppLogger.i(TAG, "BERHASIL create profil bayi di Firestore: babyDocId=$babyDocId")
                }
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan profil bayi ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncPmkSession(session: PmkSessionEntity): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) {
                AppLogger.d(TAG, "Offline: Melewati sinkronisasi sesi PMK (id=${session.id})")
                return@withContext false
            }

            try {
                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, session.babyId)

                val dto = PmkSessionFirestoreDto.fromEntity(session)
                firestore.collection(COLLECTION_BABY_RECORDS)
                    .document(babyDocId)
                    .collection(SUBCOLLECTION_PMK)
                    .document("session_${session.id}")
                    .set(dto.toMap())
                    .await()

                AppLogger.i(TAG, "BERHASIL menyinkronkan sesi PMK ke Firestore: session_${session.id}")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan sesi PMK (id=${session.id}) ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncAllPmkSessions(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val sessions = pmkSessionDao.getAllSessionsListForBaby(babyId)
                if (sessions.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                for (session in sessions) {
                    val dto = PmkSessionFirestoreDto.fromEntity(session)
                    firestore.collection(COLLECTION_BABY_RECORDS)
                        .document(babyDocId)
                        .collection(SUBCOLLECTION_PMK)
                        .document("session_${session.id}")
                        .set(dto.toMap())
                        .await()
                }
                AppLogger.i(TAG, "BERHASIL menyinkronkan ${sessions.size} sesi PMK ke Firestore")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan semua sesi PMK ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncScreening(screening: PrematureScreeningEntity): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) {
                AppLogger.d(TAG, "Offline: Melewati sinkronisasi skrining (id=${screening.id})")
                return@withContext false
            }

            try {
                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, screening.babyId)

                val dto = ScreeningFirestoreDto.fromEntity(screening)
                firestore.collection(COLLECTION_BABY_RECORDS)
                    .document(babyDocId)
                    .collection(SUBCOLLECTION_SCREENINGS)
                    .document("screening_${screening.id}")
                    .set(dto.toMap())
                    .await()

                AppLogger.i(TAG, "BERHASIL menyinkronkan skrining ke Firestore: screening_${screening.id}")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan skrining (id=${screening.id}) ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncAllScreenings(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val screenings = prematureScreeningDao.getScreeningsListForBaby(babyId)
                if (screenings.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                for (screening in screenings) {
                    val dto = ScreeningFirestoreDto.fromEntity(screening)
                    firestore.collection(COLLECTION_BABY_RECORDS)
                        .document(babyDocId)
                        .collection(SUBCOLLECTION_SCREENINGS)
                        .document("screening_${screening.id}")
                        .set(dto.toMap())
                        .await()
                }
                AppLogger.i(TAG, "BERHASIL menyinkronkan ${screenings.size} data skrining ke Firestore")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan semua data skrining ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncGrowthMeasurement(measurement: GrowthMeasurementEntity): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) {
                AppLogger.d(TAG, "Offline: Melewati sinkronisasi pengukuran pertumbuhan (id=${measurement.id})")
                return@withContext false
            }

            try {
                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, measurement.babyId)

                val dto = GrowthMeasurementFirestoreDto.fromEntity(measurement)
                firestore.collection(COLLECTION_BABY_RECORDS)
                    .document(babyDocId)
                    .collection(SUBCOLLECTION_GROWTH)
                    .document("growth_${measurement.id}")
                    .set(dto.toMap())
                    .await()

                AppLogger.i(TAG, "BERHASIL menyinkronkan pertumbuhan ke Firestore: growth_${measurement.id}")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan pertumbuhan (id=${measurement.id}) ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncAllGrowthMeasurements(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val measurements = growthMeasurementDao.getAllMeasurementsListForBaby(babyId)
                if (measurements.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                for (measurement in measurements) {
                    val dto = GrowthMeasurementFirestoreDto.fromEntity(measurement)
                    firestore.collection(COLLECTION_BABY_RECORDS)
                        .document(babyDocId)
                        .collection(SUBCOLLECTION_GROWTH)
                        .document("growth_${measurement.id}")
                        .set(dto.toMap())
                        .await()
                }
                AppLogger.i(TAG, "BERHASIL menyinkronkan ${measurements.size} data pertumbuhan ke Firestore")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan semua data pertumbuhan ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncAll(babyId: Long?): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) {
                AppLogger.d(TAG, "Offline: Melewati syncAll Firestore")
                return@withContext false
            }

            try {
                val babyEntity = if (babyId != null) {
                    babyDao.getActiveBabyOnce()
                } else {
                    babyDao.getActiveBabyOnce()
                } ?: return@withContext false

                val baby = babyEntity.toDomain()
                val targetBabyId = baby.id

                syncBabyProfile(baby)
                syncAllPmkSessions(targetBabyId)
                syncAllScreenings(targetBabyId)
                syncAllGrowthMeasurements(targetBabyId)

                AppLogger.i(TAG, "BERHASIL menjalankan syncAll komprehensif ke Firestore")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menjalankan syncAll: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }
}
