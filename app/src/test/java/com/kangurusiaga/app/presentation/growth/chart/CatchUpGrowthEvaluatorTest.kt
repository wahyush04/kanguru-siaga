package com.kangurusiaga.app.presentation.growth.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatchUpGrowthEvaluatorTest {

    @Test
    fun `baseline scenario when no prior measurements exist`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.850f,
            weightBadge = "P25 (Ideal)",
            weightGainRateGramsPerDay = null,
            lengthCm = 44.0f,
            lengthBadge = "P30 (Normal)",
            lengthGainRateCmPerWeek = null,
            headCircCm = 31.0f,
            headCircBadge = "P25 (Optimal)",
            headCircGainRateCmPerWeek = null
        )

        assertEquals("Data Dasar", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.BASELINE, summary.weightItem.level)
        assertTrue(summary.weightItem.subtitle.contains("Pengukuran pertama"))

        assertEquals("Normal", summary.lengthItem.badgeText)
        assertEquals(GrowthEvaluationLevel.NORMAL, summary.lengthItem.level)

        assertEquals("Normal", summary.headCircItem.badgeText)
        assertEquals(GrowthEvaluationLevel.NORMAL, summary.headCircItem.level)

        assertTrue(summary.clinicalNoteTitle.contains("Catatan Klinis"))
    }

    @Test
    fun `weight loss danger scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.800f,
            weightBadge = "P10 (Kecil/SGA)",
            weightGainRateGramsPerDay = -15.0f,
            lengthCm = 45.0f,
            lengthBadge = "P25 (Normal)",
            lengthGainRateCmPerWeek = 0.5f,
            headCircCm = 32.0f,
            headCircBadge = "P30 (Optimal)",
            headCircGainRateCmPerWeek = 0.5f
        )

        assertEquals("Menurun", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.DANGER, summary.weightItem.level)
        assertTrue(summary.weightItem.subtitle.contains("terjadi penurunan"))

        assertEquals(GrowthEvaluationLevel.DANGER, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Penurunan Berat Badan"))
        assertTrue(summary.clinicalNoteMessage.contains("Perhatian Segera") || summary.clinicalNoteMessage.contains("penurunan berat badan"))
    }

    @Test
    fun `flat stagnant growth warning scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.855f,
            weightBadge = "P20 (Ideal)",
            weightGainRateGramsPerDay = 0.5f, // 0.5 g/day -> stagnant
            lengthCm = 45.0f,
            lengthBadge = "P20 (Normal)",
            lengthGainRateCmPerWeek = 0.2f, // 0.2 cm/week -> flat length
            headCircCm = 32.0f,
            headCircBadge = "P20 (Optimal)",
            headCircGainRateCmPerWeek = 0.4f
        )

        assertEquals("Mendatar", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.WARNING, summary.weightItem.level)
        assertTrue(summary.weightItem.subtitle.contains("mendatar"))

        assertEquals("Mendatar", summary.lengthItem.badgeText)
        assertEquals(GrowthEvaluationLevel.WARNING, summary.lengthItem.level)

        assertEquals(GrowthEvaluationLevel.WARNING, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Laju Pertumbuhan Belum Optimal"))
    }

    @Test
    fun `suboptimal catch-up growth warning scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.900f,
            weightBadge = "P25 (Ideal)",
            weightGainRateGramsPerDay = 10.0f, // 10 g/day -> below 15 g/day target
            lengthCm = 45.5f,
            lengthBadge = "P25 (Normal)",
            lengthGainRateCmPerWeek = 0.6f,
            headCircCm = 32.5f,
            headCircBadge = "P25 (Optimal)",
            headCircGainRateCmPerWeek = 0.5f
        )

        assertEquals("Kurang Optimal", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.WARNING, summary.weightItem.level)
        assertTrue(summary.weightItem.subtitle.contains("di bawah target"))

        assertEquals(GrowthEvaluationLevel.WARNING, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Laju Pertumbuhan Belum Optimal"))
        assertTrue(summary.clinicalNoteMessage.contains("fortifikasi ASI") || summary.clinicalNoteMessage.contains("15–35 g/hari"))
    }

    @Test
    fun `optimal catch-up growth scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.950f,
            weightBadge = "P32 (Ideal)",
            weightGainRateGramsPerDay = 25.0f, // 25 g/day -> within 15-35 g/day
            lengthCm = 46.0f,
            lengthBadge = "P23 (Normal)",
            lengthGainRateCmPerWeek = 0.9f,
            headCircCm = 34.0f,
            headCircBadge = "P29 (Optimal)",
            headCircGainRateCmPerWeek = 0.7f
        )

        assertEquals("Sesuai Target", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.OPTIMAL, summary.weightItem.level)
        assertEquals("Normal", summary.lengthItem.badgeText)
        assertEquals(GrowthEvaluationLevel.NORMAL, summary.lengthItem.level)
        assertEquals("Normal", summary.headCircItem.badgeText)
        assertEquals(GrowthEvaluationLevel.NORMAL, summary.headCircItem.level)

        assertEquals(GrowthEvaluationLevel.OPTIMAL, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Sesuai Target Ideal"))
        assertTrue(summary.clinicalNoteMessage.contains("catch-up growth"))
    }

    @Test
    fun `rapid catch-up growth scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 2.100f,
            weightBadge = "P45 (Ideal)",
            weightGainRateGramsPerDay = 50.0f, // 50 g/day -> rapid > 35 g/day
            lengthCm = 47.0f,
            lengthBadge = "P40 (Normal)",
            lengthGainRateCmPerWeek = 1.5f, // > 1.4 cm/week -> very good
            headCircCm = 34.5f,
            headCircBadge = "P35 (Optimal)",
            headCircGainRateCmPerWeek = 0.8f
        )

        assertEquals("Sesuai Target", summary.weightItem.badgeText)
        assertEquals(GrowthEvaluationLevel.OPTIMAL, summary.weightItem.level)
        assertTrue(summary.weightItem.subtitle.contains("kejar tumbuh pesat"))

        assertEquals("Sangat Baik", summary.lengthItem.badgeText)
        assertEquals(GrowthEvaluationLevel.OPTIMAL, summary.lengthItem.level)

        assertEquals(GrowthEvaluationLevel.OPTIMAL, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Sangat Pesat"))
    }

    @Test
    fun `head circumference rapid jump warning scenario`() {
        val summary = CatchUpGrowthEvaluator.evaluate(
            weightKg = 1.950f,
            weightBadge = "P30 (Ideal)",
            weightGainRateGramsPerDay = 20.0f,
            lengthCm = 46.0f,
            lengthBadge = "P30 (Normal)",
            lengthGainRateCmPerWeek = 0.8f,
            headCircCm = 35.0f,
            headCircBadge = "P50 (Optimal)",
            headCircGainRateCmPerWeek = 1.4f // > 1.1 cm/week -> rapid cranial jump
        )

        assertEquals("Perlu Waspada", summary.headCircItem.badgeText)
        assertEquals(GrowthEvaluationLevel.WARNING, summary.headCircItem.level)
        assertTrue(summary.headCircItem.subtitle.contains("Lonjakan"))

        assertEquals(GrowthEvaluationLevel.WARNING, summary.overallLevel)
        assertTrue(summary.clinicalNoteTitle.contains("Lingkar Kepala"))
        assertTrue(summary.clinicalNoteMessage.contains("neurosonografi") || summary.clinicalNoteMessage.contains("USG kepala"))
    }
}

