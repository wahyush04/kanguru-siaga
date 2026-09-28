package com.kangurusiaga.app.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FeedingScheduleTest {

    @Test
    fun formattedTime_returnsPaddedTimeString() {
        val schedule1 = FeedingSchedule(hour = 6, minute = 0, volumeMl = 30)
        val schedule2 = FeedingSchedule(hour = 14, minute = 5, volumeMl = 35)

        assertThat(schedule1.formattedTime).isEqualTo("06:00")
        assertThat(schedule2.formattedTime).isEqualTo("14:05")
    }

    @Test
    fun cycleSortKey_sortsFrom0600CaregiverCycle() {
        val slots = listOf(
            FeedingSchedule(hour = 4, minute = 0, volumeMl = 30),
            FeedingSchedule(hour = 0, minute = 0, volumeMl = 30),
            FeedingSchedule(hour = 6, minute = 0, volumeMl = 30),
            FeedingSchedule(hour = 12, minute = 0, volumeMl = 30),
            FeedingSchedule(hour = 22, minute = 0, volumeMl = 30)
        )

        val sorted = slots.sortedBy { it.cycleSortKey }

        assertThat(sorted.map { it.formattedTime }).containsExactly(
            "06:00",
            "12:00",
            "22:00",
            "00:00",
            "04:00"
        ).inOrder()
    }

    @Test
    fun feedingMethod_fromString_handlesValidAndInvalid() {
        assertThat(FeedingMethod.fromString("OGT_NGT")).isEqualTo(FeedingMethod.OGT_NGT)
        assertThat(FeedingMethod.fromString("CUP")).isEqualTo(FeedingMethod.CUP)
        assertThat(FeedingMethod.fromString("SPOON")).isEqualTo(FeedingMethod.SPOON)
        assertThat(FeedingMethod.fromString("UNKNOWN")).isEqualTo(FeedingMethod.OGT_NGT)
        assertThat(FeedingMethod.fromString(null)).isEqualTo(FeedingMethod.OGT_NGT)
    }

    @Test
    fun repeatType_fromString_handlesValidAndInvalid() {
        assertThat(RepeatType.fromString("DAILY")).isEqualTo(RepeatType.DAILY)
        assertThat(RepeatType.fromString("ONCE")).isEqualTo(RepeatType.ONCE)
        assertThat(RepeatType.fromString("UNKNOWN")).isEqualTo(RepeatType.DAILY)
        assertThat(RepeatType.fromString(null)).isEqualTo(RepeatType.DAILY)
    }
}
