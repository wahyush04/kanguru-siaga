package com.kangurusiaga.app.data.remote.config

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PmkRemoteConfigManagerTest {

    @Test
    fun mode1_showsOnlyOnceEver() {
        assertThat(
            PmkRemoteConfigManager.shouldShowVerificationPopup(
                mode = 1,
                hasSeenOnce = false,
                lastVerifiedDateKey = null,
                todayDateKey = "2026-11-28"
            )
        ).isTrue()

        assertThat(
            PmkRemoteConfigManager.shouldShowVerificationPopup(
                mode = 1,
                hasSeenOnce = true,
                lastVerifiedDateKey = "2026-11-27",
                todayDateKey = "2026-11-28"
            )
        ).isFalse()
    }

    @Test
    fun mode2_showsOncePerDay() {
        // Different day -> show
        assertThat(
            PmkRemoteConfigManager.shouldShowVerificationPopup(
                mode = 2,
                hasSeenOnce = true,
                lastVerifiedDateKey = "2026-11-27",
                todayDateKey = "2026-11-28"
            )
        ).isTrue()

        // Same day -> do not show
        assertThat(
            PmkRemoteConfigManager.shouldShowVerificationPopup(
                mode = 2,
                hasSeenOnce = true,
                lastVerifiedDateKey = "2026-11-28",
                todayDateKey = "2026-11-28"
            )
        ).isFalse()
    }

    @Test
    fun mode3_showsEveryTime() {
        assertThat(
            PmkRemoteConfigManager.shouldShowVerificationPopup(
                mode = 3,
                hasSeenOnce = true,
                lastVerifiedDateKey = "2026-11-28",
                todayDateKey = "2026-11-28"
            )
        ).isTrue()
    }
}
