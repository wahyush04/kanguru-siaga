package com.kangurusiaga.app.core.common

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class CrashlyticsLoggerTest {

    private val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `log delegates message to FirebaseCrashlytics`() {
        CrashlyticsLogger.log("User initiated PMK session")
        verify { mockCrashlytics.log("User initiated PMK session") }
    }

    @Test
    fun `recordException delegates throwable to FirebaseCrashlytics`() {
        val testException = IllegalStateException("Test error")
        CrashlyticsLogger.recordException(testException)
        verify { mockCrashlytics.recordException(testException) }
    }

    @Test
    fun `setUserId delegates userId to FirebaseCrashlytics`() {
        CrashlyticsLogger.setUserId("user_12345")
        verify { mockCrashlytics.setUserId("user_12345") }
    }

    @Test
    fun `setCustomKey delegates key-value to FirebaseCrashlytics`() {
        CrashlyticsLogger.setCustomKey("screen", "HomeScreen")
        CrashlyticsLogger.setCustomKey("is_active", true)
        CrashlyticsLogger.setCustomKey("count", 42)

        verify { mockCrashlytics.setCustomKey("screen", "HomeScreen") }
        verify { mockCrashlytics.setCustomKey("is_active", true) }
        verify { mockCrashlytics.setCustomKey("count", 42) }
    }

    @Test
    fun `forceTestCrash throws RuntimeException`() {
        assertThrows(RuntimeException::class.java) {
            CrashlyticsLogger.forceTestCrash()
        }
    }
}
