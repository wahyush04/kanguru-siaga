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
        const val SUBCOLLECTION_BABY_DATA = "baby_data"
        const val SUBCOLLECTION_PMK_DAILY = "pmk_daily_records"
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
                syncBabyProfile(baby)
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
                // 1. Sinkronkan profil utama ibu & bayi ke baby_records/{babyDocId} (root & baby_data)
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
                val parentDocRef = firestore.collection(COLLECTION_BABY_RECORDS).document(babyDocId)

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

                // 1. Simpan metadata pada dokumen induk user
                val rootMeta = mapOf(
                    "userId" to babyDocId,
                    "babyId" to baby.id,
                    "babyName" to baby.name,
                    "motherName" to baby.motherName,
                    "motherPhoneNumber" to baby.motherPhoneNumber,
                    "environment" to BuildConfig.ENVIRONMENT,
                    "appVersion" to BuildConfig.VERSION_NAME,
                    "updatedAtServer" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                parentDocRef.set(rootMeta, com.google.firebase.firestore.SetOptions.merge()).await()

                // 2. Simpan profil ke subkoleksi baby_data/profile
                val babyDataDocRef = parentDocRef.collection(SUBCOLLECTION_BABY_DATA).document("profile")
                val snapshot = babyDataDocRef.get().await()
                if (snapshot.exists()) {
                    val existingCreatedAt = snapshot.get("createdAtServer")
                    val map = dto.toMap(isNewDocument = false).toMutableMap()
                    if (existingCreatedAt != null) {
                        map["createdAtServer"] = existingCreatedAt
                    }
                    babyDataDocRef.set(map).await()
                    AppLogger.i(TAG, "BERHASIL update subkoleksi baby_data/profile: babyDocId=$babyDocId")
                } else {
                    babyDataDocRef.set(dto.toMap(isNewDocument = true)).await()
                    AppLogger.i(TAG, "BERHASIL create subkoleksi baby_data/profile: babyDocId=$babyDocId")
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
        return syncAllPmkSessions(session.babyId)
    }

    override suspend fun syncAllPmkSessions(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val sessions = pmkSessionDao.getAllSessionsListForBaby(babyId)
                if (sessions.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                // Kelompokkan sesi PMK berdasarkan tanggal hari (format ddMMyyyy)
                val sdfDocId = java.text.SimpleDateFormat("ddMMyyyy", java.util.Locale.US)
                val sdfFormatted = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale("id", "ID"))

                val groupedByDay = sessions.groupBy { sdfDocId.format(java.util.Date(it.startTimeEpoch)) }

                for ((dayKey, daySessions) in groupedByDay) {
                    val firstSession = daySessions.minByOrNull { it.startTimeEpoch } ?: daySessions.first()
                    val totalDuration = daySessions.sumOf { it.durationMinutes }
                    val formattedDate = try {
                        sdfFormatted.format(java.util.Date(firstSession.startTimeEpoch))
                    } catch (_: Exception) {
                        ""
                    }

                    val dailyMap = mapOf(
                        "sessionId" to firstSession.id,
                        "dateEpochMillis" to firstSession.startTimeEpoch,
                        "dateFormatted" to formattedDate,
                        "totalDurationMinutes" to totalDuration,
                        "syncedAtEpochMillis" to System.currentTimeMillis(),
                        "updatedAtServer" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    val docName = "date_$dayKey"
                    firestore.collection(COLLECTION_BABY_RECORDS)
                        .document(babyDocId)
                        .collection(SUBCOLLECTION_PMK_DAILY)
                        .document(docName)
                        .set(dailyMap)
                        .await()
                }

                AppLogger.i(TAG, "BERHASIL menyinkronkan rekam harian PMK ke $SUBCOLLECTION_PMK_DAILY (${groupedByDay.size} hari)")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan rekam harian PMK ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncScreening(screening: PrematureScreeningEntity): Boolean {
        return syncAllScreenings(screening.babyId)
    }

    override suspend fun syncAllScreenings(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val screenings = prematureScreeningDao.getScreeningsListForBaby(babyId)
                if (screenings.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                var neuroMap: Map<String, Any?>? = null
                var oftamologiMap: Map<String, Any?>? = null
                var audiologiMap: Map<String, Any?>? = null
                var tiroidMap: Map<String, Any?>? = null
                val additionalList = mutableListOf<Map<String, Any?>>()

                for (s in screenings) {
                    val dto = ScreeningFirestoreDto.fromEntity(s)
                    val map = dto.toMap()
                    when (s.screeningType) {
                        "USG_HEAD" -> neuroMap = map
                        "ROP_EYE", "ROP" -> oftamologiMap = map
                        "HEARING_OAE", "HEARING_OAE_BERA" -> audiologiMap = map
                        "THYROID_FUNCTION", "THYROID" -> tiroidMap = map
                        else -> additionalList.add(map)
                    }
                }

                val essentialDoc = mutableMapOf<String, Any?>(
                    "syncedAtEpochMillis" to System.currentTimeMillis(),
                    "updatedAtServer" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                if (neuroMap != null) essentialDoc["screening_neurosonografi"] = neuroMap
                if (oftamologiMap != null) essentialDoc["screening_oftamologi"] = oftamologiMap
                if (audiologiMap != null) essentialDoc["screening_audiologi"] = audiologiMap
                if (tiroidMap != null) essentialDoc["screening_tiroid"] = tiroidMap

                firestore.collection(COLLECTION_BABY_RECORDS)
                    .document(babyDocId)
                    .collection(SUBCOLLECTION_SCREENINGS)
                    .document("essential_screening")
                    .set(essentialDoc, com.google.firebase.firestore.SetOptions.merge())
                    .await()

                val additionalDoc = mapOf(
                    "items" to additionalList,
                    "additional_screening" to additionalList,
                    "syncedAtEpochMillis" to System.currentTimeMillis(),
                    "updatedAtServer" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                firestore.collection(COLLECTION_BABY_RECORDS)
                    .document(babyDocId)
                    .collection(SUBCOLLECTION_SCREENINGS)
                    .document("additional_screening")
                    .set(additionalDoc, com.google.firebase.firestore.SetOptions.merge())
                    .await()

                AppLogger.i(TAG, "BERHASIL menyinkronkan ${screenings.size} data skrining ke $SUBCOLLECTION_SCREENINGS")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "GAGAL menyinkronkan semua data skrining ke Firestore: ${e.message}", e)
                CrashlyticsLogger.recordException(e)
                false
            }
        }
    }

    override suspend fun syncGrowthMeasurement(measurement: GrowthMeasurementEntity): Boolean {
        return syncAllGrowthMeasurements(measurement.babyId)
    }

    override suspend fun syncAllGrowthMeasurements(babyId: Long): Boolean {
        return withContext(ioDispatcher) {
            if (!networkMonitor.isOnline()) return@withContext false
            try {
                val measurements = growthMeasurementDao.getAllMeasurementsListForBaby(babyId)
                if (measurements.isEmpty()) return@withContext true

                val babyDocId = userPreferencesRepository.getOrCreateFirestoreBabyDocId()
                ensureParentBabyDocExists(babyDocId, babyId)

                // Kelompokkan data pengukuran berdasarkan PMA weeks (integer) agar record bernama pma_week_XX
                val groupedByPma = measurements.groupBy { it.pmaWeeks.toInt() }

                for ((pmaWeekInt, list) in groupedByPma) {
                    val first = list.first()
                    var weightVal = 0.0
                    var lengthVal = 0.0
                    var headVal = 0.0

                    for (m in list) {
                        when (m.parameter.uppercase()) {
                            "WEIGHT" -> {
                                weightVal = if (m.value < 50f) (m.value * 1000f).toDouble() else m.value.toDouble()
                            }
                            "LENGTH" -> {
                                lengthVal = m.value.toDouble()
                            }
                            "HEAD_CIRCUMFERENCE" -> {
                                headVal = m.value.toDouble()
                            }
                        }
                    }

                    val formattedDate = try {
                        java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date(first.measurementDateEpochMillis))
                    } catch (_: Exception) {
                        ""
                    }

                    val structuredDto = StructuredGrowthRecordDto(
                        measurementId = first.id,
                        measurementDateEpochMillis = first.measurementDateEpochMillis,
                        measurementDateFormatted = formattedDate,
                        pmaWeeks = pmaWeekInt.toFloat(),
                        chronologicalAgeWeeks = first.chronologicalAgeWeeks,
                        weightValue = weightVal,
                        weightUnit = "gram",
                        lengthValue = lengthVal,
                        lengthUnit = "cm",
                        headValue = headVal,
                        headUnit = "cm"
                    )

                    val docName = "pma_week_$pmaWeekInt"
                    firestore.collection(COLLECTION_BABY_RECORDS)
                        .document(babyDocId)
                        .collection(SUBCOLLECTION_GROWTH)
                        .document(docName)
                        .set(structuredDto.toMap())
                        .await()
                }

                AppLogger.i(TAG, "BERHASIL menyinkronkan data pertumbuhan ke $SUBCOLLECTION_GROWTH berdasarkan PMA weeks")
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
