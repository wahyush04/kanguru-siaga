package com.kangurusiaga.app.domain.util

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import org.junit.Test
import kotlin.math.abs

class FentonCalculationUnitTest {

    @Test
    fun testColeLmsZScoreAndValueInversion() {
        // Sample LMS parameters: GA 23 Male weight
        val l = 0.79707872
        val m = 592.5751
        val s = 0.150539409

        // Test at Median (X = M) -> Z should be exactly 0.0
        val zAtMedian = FentonCalculator.calculateZScore(m, l, m, s)
        assertThat(abs(zAtMedian)).isLessThan(1e-6)

        // Test value from Z=0 -> X should be M
        val xAtZero = FentonCalculator.calculateValueFromZ(0.0, l, m, s)
        assertThat(abs(xAtZero - m)).isLessThan(1e-4)

        // Test round-trip: calculate Z from arbitrary X, then calculate X back from Z
        val testX = 650.0
        val zScore = FentonCalculator.calculateZScore(testX, l, m, s)
        val invertedX = FentonCalculator.calculateValueFromZ(zScore, l, m, s)
        assertThat(abs(invertedX - testX)).isLessThan(1e-4)
    }

    @Test
    fun testColeLmsSpecialCaseLZero() {
        val l = 0.0
        val m = 100.0
        val s = 0.1

        val x = 110.0
        val z = FentonCalculator.calculateZScore(x, l, m, s)
        val invertedX = FentonCalculator.calculateValueFromZ(z, l, m, s)
        assertThat(abs(invertedX - x)).isLessThan(1e-4)
    }

    @Test
    fun testClinicalClassifications() {
        // SGA: Z < -1.28155 (< P10)
        assertThat(FentonCalculator.getClinicalClassification(-1.50)).isEqualTo("SGA")
        assertThat(FentonCalculator.getClinicalClassification(-1.29)).isEqualTo("SGA")

        // AGA: -1.28155 <= Z <= 1.28155 (P10 to P90)
        assertThat(FentonCalculator.getClinicalClassification(-1.28)).isEqualTo("AGA")
        assertThat(FentonCalculator.getClinicalClassification(0.0)).isEqualTo("AGA")
        assertThat(FentonCalculator.getClinicalClassification(1.28)).isEqualTo("AGA")

        // LGA: Z > 1.28155 (> P90)
        assertThat(FentonCalculator.getClinicalClassification(1.29)).isEqualTo("LGA")
        assertThat(FentonCalculator.getClinicalClassification(2.10)).isEqualTo("LGA")
    }

    @Test
    fun testPercentileBadges() {
        assertThat(FentonCalculator.getPercentileBadge(-2.0)).isEqualTo("< P3")
        assertThat(FentonCalculator.getPercentileBadge(-1.5)).isEqualTo("P3 - P10")
        assertThat(FentonCalculator.getPercentileBadge(-0.5)).isEqualTo("P10 - P50")
        assertThat(FentonCalculator.getPercentileBadge(0.0)).isEqualTo("P50")
        assertThat(FentonCalculator.getPercentileBadge(0.5)).isEqualTo("P50 - P90")
        assertThat(FentonCalculator.getPercentileBadge(1.5)).isEqualTo("P90 - P97")
        assertThat(FentonCalculator.getPercentileBadge(2.0)).isEqualTo("> P97")
    }

    @Test
    fun testFentonReferenceDataDynamicCalculation() {
        // Test Male weight at week 23
        // Known Fenton 2013: M = 593 g (~0.593 kg), P3 ~ 0.430 kg, P97 ~ 0.765 kg
        val maleWeightCurve = FentonReferenceData.getCurvePoints(GrowthParameter.WEIGHT, Gender.MALE)
        val week23 = maleWeightCurve.first { it.pmaWeeks == 23f }

        assertThat(week23.p50).isWithin(0.01f).of(0.593f)
        assertThat(week23.p3).isWithin(0.01f).of(0.430f)
        assertThat(week23.p97).isWithin(0.01f).of(0.765f)

        // Evaluate percentile dynamically
        val eval = FentonReferenceData.evaluatePercentile(
            parameter = GrowthParameter.WEIGHT,
            gender = Gender.MALE,
            pmaWeeks = 23f,
            value = 0.593f // exactly median
        )

        assertThat(eval.percentileBadge).isEqualTo("P50")
        assertThat(eval.isNormal).isTrue()
        assertThat(eval.clinicalClassification).isEqualTo("AGA")
    }

    @Test
    fun testZScoreToPercentilePercentage() {
        // Z = 0 -> 50%
        val p50Pct = FentonCalculator.zScoreToPercentilePercentage(0.0)
        assertThat(p50Pct).isWithin(0.1).of(50.0)

        // Z = -1.28155 -> ~10%
        val p10Pct = FentonCalculator.zScoreToPercentilePercentage(-1.28155)
        assertThat(p10Pct).isWithin(0.2).of(10.0)

        // Z = 1.28155 -> ~90%
        val p90Pct = FentonCalculator.zScoreToPercentilePercentage(1.28155)
        assertThat(p90Pct).isWithin(0.2).of(90.0)
    }
}
