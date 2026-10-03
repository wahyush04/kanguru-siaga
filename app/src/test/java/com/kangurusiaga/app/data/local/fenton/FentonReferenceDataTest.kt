package com.kangurusiaga.app.data.local.fenton

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.util.PmaCalculator
import org.junit.Test
import java.util.concurrent.TimeUnit

class FentonReferenceDataTest {

    @Test
    fun getCurvePoints_has29PointsFrom22to50Weeks() {
        val params = listOf(
            GrowthParameter.WEIGHT,
            GrowthParameter.LENGTH,
            GrowthParameter.HEAD_CIRCUMFERENCE
        )
        val genders = listOf(Gender.MALE, Gender.FEMALE)

        for (param in params) {
            for (gender in genders) {
                val points = FentonReferenceData.getCurvePoints(param, gender)
                assertThat(points).hasSize(29)
                assertThat(points.first().pmaWeeks).isEqualTo(22f)
                assertThat(points.last().pmaWeeks).isEqualTo(50f)
            }
        }
    }

    @Test
    fun fenton2013_referenceValues_matchOfficialDatasetAt40Weeks() {
        // Male at 40 weeks
        val maleWeight40 = FentonReferenceData.interpolatePoint(GrowthParameter.WEIGHT, Gender.MALE, 40f)
        assertThat(maleWeight40.p50.toDouble()).isWithin(0.01).of(3.568)

        val maleLength40 = FentonReferenceData.interpolatePoint(GrowthParameter.LENGTH, Gender.MALE, 40f)
        assertThat(maleLength40.p50.toDouble()).isWithin(0.01).of(51.16)

        val maleHead40 = FentonReferenceData.interpolatePoint(GrowthParameter.HEAD_CIRCUMFERENCE, Gender.MALE, 40f)
        assertThat(maleHead40.p50.toDouble()).isWithin(0.01).of(35.04)

        // Female at 40 weeks
        val femaleWeight40 = FentonReferenceData.interpolatePoint(GrowthParameter.WEIGHT, Gender.FEMALE, 40f)
        assertThat(femaleWeight40.p50.toDouble()).isWithin(0.01).of(3.415)

        val femaleLength40 = FentonReferenceData.interpolatePoint(GrowthParameter.LENGTH, Gender.FEMALE, 40f)
        assertThat(femaleLength40.p50.toDouble()).isWithin(0.01).of(50.44)

        val femaleHead40 = FentonReferenceData.interpolatePoint(GrowthParameter.HEAD_CIRCUMFERENCE, Gender.FEMALE, 40f)
        assertThat(femaleHead40.p50.toDouble()).isWithin(0.01).of(34.70)
    }

    @Test
    fun linearInterpolation_computesAccurateMidpoint() {
        val pt32 = FentonReferenceData.interpolatePoint(GrowthParameter.WEIGHT, Gender.MALE, 32f)
        val pt33 = FentonReferenceData.interpolatePoint(GrowthParameter.WEIGHT, Gender.MALE, 33f)
        val pt32Half = FentonReferenceData.interpolatePoint(GrowthParameter.WEIGHT, Gender.MALE, 32.5f)

        val expectedMidP50 = ((pt32.p50 + pt33.p50) / 2f).toDouble()
        assertThat(pt32Half.p50.toDouble()).isWithin(0.01).of(expectedMidP50)
    }

    @Test
    fun evaluatePercentile_categorizesCorrectly() {
        // P50 value at 40 weeks (3.568 kg for boy)
        val evalMedian = FentonReferenceData.evaluatePercentile(
            GrowthParameter.WEIGHT,
            Gender.MALE,
            40f,
            3.568f
        )
        assertThat(evalMedian.percentileBadge).isEqualTo("P50")
        assertThat(evalMedian.isNormal).isTrue()

        // Very low value < P3 (e.g. 2.0 kg at 40 weeks)
        val evalLow = FentonReferenceData.evaluatePercentile(
            GrowthParameter.WEIGHT,
            Gender.MALE,
            40f,
            2.0f
        )
        assertThat(evalLow.percentileBadge).isEqualTo("< P3")
        assertThat(evalLow.isNormal).isFalse()

        // Very high value > P97 (e.g. 5.5 kg at 40 weeks)
        val evalHigh = FentonReferenceData.evaluatePercentile(
            GrowthParameter.WEIGHT,
            Gender.MALE,
            40f,
            5.5f
        )
        assertThat(evalHigh.percentileBadge).isEqualTo("> P97")
        assertThat(evalHigh.isNormal).isFalse()
    }

    @Test
    fun pmaCalculator_computesChronologicalAgeAndPmaCorrectly() {
        val birthMillis = 1000000000000L
        val measurementMillis = birthMillis + TimeUnit.DAYS.toMillis(14) // 2 weeks later
        val gestationalAgeAtBirth = 32

        val calc = PmaCalculator.calculate(
            birthDateEpochMillis = birthMillis,
            measurementDateEpochMillis = measurementMillis,
            gestationalAgeAtBirthWeeks = gestationalAgeAtBirth
        )

        assertThat(calc.chronologicalDays).isEqualTo(14)
        assertThat(calc.chronologicalWeeks).isEqualTo(2.0f)
        assertThat(calc.pmaWeeks).isEqualTo(34.0f)
        assertThat(calc.chronologicalDisplay).isEqualTo("2 Minggu")
        assertThat(calc.pmaDisplay).isEqualTo("34 Minggu")
    }
}
