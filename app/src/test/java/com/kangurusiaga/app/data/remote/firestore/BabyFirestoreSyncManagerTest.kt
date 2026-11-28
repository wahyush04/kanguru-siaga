package com.kangurusiaga.app.data.remote.firestore

import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.NetworkMonitor
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.dao.PrematureScreeningDao
import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.data.local.entity.PrematureScreeningEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BabyFirestoreSyncManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val firestore: FirebaseFirestore = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    private val userPreferencesRepository: UserPreferencesRepository = mockk(relaxed = true)
    private val babyDao: BabyDao = mockk(relaxed = true)
    private val pmkSessionDao: PmkSessionDao = mockk(relaxed = true)
    private val prematureScreeningDao: PrematureScreeningDao = mockk(relaxed = true)
    private val growthMeasurementDao: GrowthMeasurementDao = mockk(relaxed = true)

    private val collectionRef: CollectionReference = mockk()
    private val subCollectionRef: CollectionReference = mockk()
    private val documentRef: DocumentReference = mockk()
    private val subDocRef: DocumentReference = mockk()
    private val docSnapshot: DocumentSnapshot = mockk()

    private lateinit var syncManager: BabyFirestoreSyncManagerImpl

    @Before
    fun setUp() {
        coEvery { userPreferencesRepository.getOrCreateFirestoreBabyDocId() } returns "doc_test_123"

        every { firestore.collection(any()) } returns collectionRef
        every { collectionRef.document(any()) } returns documentRef
        every { collectionRef.add(any()) } returns Tasks.forResult(documentRef)

        every { documentRef.id } returns "doc_12345"
        every { documentRef.get() } returns Tasks.forResult(docSnapshot)
        every { documentRef.set(any()) } returns Tasks.forResult(null)
        every { documentRef.set(any(), any()) } returns Tasks.forResult(null)
        every { documentRef.update(any<Map<String, Any?>>()) } returns Tasks.forResult(null)
        every { documentRef.collection(any()) } returns subCollectionRef

        every { subCollectionRef.document(any()) } returns subDocRef
        every { subDocRef.get() } returns Tasks.forResult(docSnapshot)
        every { subDocRef.set(any()) } returns Tasks.forResult(null)
        every { subDocRef.set(any(), any()) } returns Tasks.forResult(null)

        every { docSnapshot.exists() } returns false

        syncManager = BabyFirestoreSyncManagerImpl(
            context = mockk(relaxed = true),
            firestore = firestore,
            networkMonitor = networkMonitor,
            userPreferencesRepository = userPreferencesRepository,
            babyDao = babyDao,
            pmkSessionDao = pmkSessionDao,
            prematureScreeningDao = prematureScreeningDao,
            growthMeasurementDao = growthMeasurementDao,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun syncOnboardingBaby_whenOffline_skipsFirestoreAndReturnsFalse() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns false

        val baby = Baby(
            name = "Baby Kenzo",
            gender = Gender.MALE,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 2100,
            gestationalAgeWeeks = 34
        )

        val result = syncManager.syncOnboardingBaby(baby, currentWeightGram = 2200)

        assertThat(result).isFalse()
        verify(exactly = 0) { firestore.collection(any()) }
    }

    @Test
    fun syncOnboardingBaby_whenOnline_sendsDataToFirestore() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true

        val capturedMap = slot<Map<String, Any?>>()
        every { subDocRef.set(capture(capturedMap)) } returns Tasks.forResult(null)

        val baby = Baby(
            id = 1L,
            name = "Siti Aisyah",
            motherName = "Ny. Fatimah",
            motherPhoneNumber = "081234567890",
            gender = Gender.FEMALE,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 1950,
            gestationalAgeWeeks = 33
        )

        coEvery { prematureScreeningDao.getScreeningsListForBaby(1L) } returns emptyList()
        coEvery { growthMeasurementDao.getAllMeasurementsListForBaby(1L) } returns emptyList()

        val result = syncManager.syncOnboardingBaby(baby, currentWeightGram = 2050)

        assertThat(result).isTrue()
        verify(atLeast = 1) { subDocRef.set(any()) }

        val map = capturedMap.captured
        assertThat(map["babyName"]).isEqualTo("Siti Aisyah")
        assertThat(map["motherName"]).isEqualTo("Ny. Fatimah")
        assertThat(map["motherPhoneNumber"]).isEqualTo("081234567890")
        assertThat(map["birthDateEpochMillis"]).isEqualTo(1700000000000L)
        assertThat(map["gestationalAgeWeeks"]).isEqualTo(33)
        assertThat(map["birthWeightGram"]).isEqualTo(1950)
        assertThat(map["currentWeightGram"]).isEqualTo(2050)
        assertThat(map["gender"]).isEqualTo("PEREMPUAN")
    }

    @Test
    fun syncOnboardingBaby_whenFirestoreThrowsException_handlesGracefullyAndReturnsFalse() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { documentRef.set(any(), any()) } throws RuntimeException("Simulated Firestore Exception")
        every { subDocRef.get() } throws RuntimeException("Simulated Firestore Exception")

        val baby = Baby(
            name = "Baby Test",
            gender = Gender.MALE,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 2000,
            gestationalAgeWeeks = 32
        )

        val result = syncManager.syncOnboardingBaby(baby, currentWeightGram = 2000)

        // Tidak boleh crash atau throw exception ke caller
        assertThat(result).isFalse()
    }

    @Test
    fun syncBabyProfile_whenOnline_createsOrUpdatesDocument() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns false

        val baby = Baby(
            id = 1L,
            name = "Kenzo",
            motherName = "Ny. Sarah",
            motherPhoneNumber = "081234567890",
            gender = Gender.MALE,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 32
        )

        val result = syncManager.syncBabyProfile(baby)

        assertThat(result).isTrue()
        verify(atLeast = 1) { subDocRef.set(any()) }
    }

    @Test
    fun syncPmkSession_whenOnline_syncsToSubcollection() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns true

        val session = PmkSessionEntity(
            id = 10L,
            babyId = 1L,
            startTimeEpoch = 1700000000000L,
            endTimeEpoch = 1700003600000L,
            durationMinutes = 60,
            source = "TIMER",
            currentCaregiver = "IBU"
        )
        coEvery { pmkSessionDao.getAllSessionsListForBaby(1L) } returns listOf(session)

        val result = syncManager.syncPmkSession(session)

        assertThat(result).isTrue()
        verify(atLeast = 1) { subDocRef.set(any()) }
    }

    @Test
    fun syncScreening_whenOnline_syncsToSubcollection() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns true

        val screening = PrematureScreeningEntity(
            id = 5L,
            babyId = 1L,
            screeningType = "USG_HEAD",
            title = "USG Kepala",
            categoryBadge = "Neurosonografi",
            targetTimingLabel = "Usia 2 Minggu",
            targetDateEpoch = 1700000000000L,
            indication = "Bayi prematur",
            clinicalGoal = "Deteksi IVH",
            clinicOrLab = "Poli Anak",
            isCompleted = false
        )
        coEvery { prematureScreeningDao.getScreeningsListForBaby(1L) } returns listOf(screening)

        val result = syncManager.syncScreening(screening)

        assertThat(result).isTrue()
        verify(atLeast = 1) { subDocRef.set(any(), any()) }
    }

    @Test
    fun syncScreening_withCustomScreening_syncsToAdditionalScreeningDoc() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns true

        val essentialScreening = PrematureScreeningEntity(
            id = 5L,
            babyId = 1L,
            screeningType = "USG_HEAD",
            title = "USG Kepala",
            categoryBadge = "Neurosonografi",
            targetTimingLabel = "Usia 2 Minggu",
            targetDateEpoch = 1700000000000L,
            indication = "Bayi prematur",
            clinicalGoal = "Deteksi IVH",
            clinicOrLab = "Poli Anak",
            isCompleted = false
        )
        val customScreening = PrematureScreeningEntity(
            id = 6L,
            babyId = 1L,
            screeningType = "CUSTOM",
            title = "Evaluasi Neurologi Anak",
            categoryBadge = "Skrining Tambahan Kustom",
            targetTimingLabel = "Pemeriksaan Spesialis",
            targetDateEpoch = 1700000000000L,
            indication = "Evaluasi neurologi",
            clinicalGoal = "Evaluasi tumbuh kembang",
            clinicOrLab = "Poli Neurologi",
            isCompleted = true,
            findings = "Hasil skrining kustom neurologi"
        )
        coEvery { prematureScreeningDao.getScreeningsListForBaby(1L) } returns listOf(essentialScreening, customScreening)

        val result = syncManager.syncScreening(customScreening)

        assertThat(result).isTrue()
        verify(atLeast = 2) { subDocRef.set(any(), any()) }
    }

    @Test
    fun syncGrowthMeasurement_whenOnline_syncsToSubcollection() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns true

        val measurement = GrowthMeasurementEntity(
            id = 7L,
            babyId = 1L,
            parameter = "WEIGHT",
            measurementDateEpochMillis = 1700000000000L,
            chronologicalAgeWeeks = 2.0f,
            pmaWeeks = 34.0f,
            value = 1950.0f,
            unit = "gram"
        )
        coEvery { growthMeasurementDao.getAllMeasurementsListForBaby(1L) } returns listOf(measurement)

        val result = syncManager.syncGrowthMeasurement(measurement)

        assertThat(result).isTrue()
        verify(atLeast = 1) { subDocRef.set(any()) }
    }

    @Test
    fun syncAll_whenOnline_syncsAllCategories() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { docSnapshot.exists() } returns true

        val babyEntity = BabyEntity(
            id = 1L,
            name = "Baby Kenzo",
            motherName = "Ny. Sarah",
            motherPhoneNumber = "081234567890",
            gender = Gender.MALE.name,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 32
        )
        coEvery { babyDao.getActiveBabyOnce() } returns babyEntity
        coEvery { pmkSessionDao.getAllSessionsListForBaby(1L) } returns emptyList()
        coEvery { prematureScreeningDao.getScreeningsListForBaby(1L) } returns emptyList()
        coEvery { growthMeasurementDao.getAllMeasurementsListForBaby(1L) } returns emptyList()

        val result = syncManager.syncAll(1L)

        assertThat(result).isTrue()
    }
}
