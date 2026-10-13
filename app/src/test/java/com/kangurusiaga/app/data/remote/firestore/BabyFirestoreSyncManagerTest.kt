package com.kangurusiaga.app.data.remote.firestore

import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kangurusiaga.app.core.common.NetworkMonitor
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
    private val collectionRef: CollectionReference = mockk()
    private val documentRef: DocumentReference = mockk()

    private lateinit var syncManager: BabyFirestoreSyncManagerImpl

    @Before
    fun setUp() {
        every { firestore.collection(BabyFirestoreSyncManagerImpl.COLLECTION_BABY_PROFILES) } returns collectionRef
        every { documentRef.id } returns "doc_12345"
        syncManager = BabyFirestoreSyncManagerImpl(
            firestore = firestore,
            networkMonitor = networkMonitor,
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
    fun syncOnboardingBaby_whenOnline_sendsAll6RequiredFieldsToFirestore() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true

        val capturedMap = slot<Map<String, Any?>>()
        every { collectionRef.add(capture(capturedMap)) } returns Tasks.forResult(documentRef)

        val baby = Baby(
            name = "Siti Aisyah",
            gender = Gender.FEMALE,
            birthDateEpochMillis = 1700000000000L,
            birthWeightGram = 1950,
            gestationalAgeWeeks = 33
        )

        val result = syncManager.syncOnboardingBaby(baby, currentWeightGram = 2050)

        assertThat(result).isTrue()
        verify(exactly = 1) { collectionRef.add(any()) }

        val map = capturedMap.captured
        assertThat(map["babyName"]).isEqualTo("Siti Aisyah")
        assertThat(map["birthDateEpochMillis"]).isEqualTo(1700000000000L)
        assertThat(map["gestationalAgeWeeks"]).isEqualTo(33)
        assertThat(map["birthWeightGram"]).isEqualTo(1950)
        assertThat(map["currentWeightGram"]).isEqualTo(2050)
        assertThat(map["gender"]).isEqualTo("PEREMPUAN")
    }

    @Test
    fun syncOnboardingBaby_whenFirestoreThrowsException_handlesGracefullyAndReturnsFalse() = runTest(testDispatcher) {
        every { networkMonitor.isOnline() } returns true
        every { collectionRef.add(any()) } throws RuntimeException("Simulated Firestore Exception")

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
}
