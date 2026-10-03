package com.kangurusiaga.app.domain.util

import java.util.concurrent.TimeUnit

object PmaCalculator {

    data class AgeCalculation(
        val chronologicalDays: Long,
        val chronologicalWeeks: Float,
        val pmaWeeks: Float,
        val chronologicalDisplay: String,
        val pmaDisplay: String
    )

    fun calculate(
        birthDateEpochMillis: Long,
        measurementDateEpochMillis: Long,
        gestationalAgeAtBirthWeeks: Int
    ): AgeCalculation {
        val diffMillis = maxOf(0L, measurementDateEpochMillis - birthDateEpochMillis)
        val chronologicalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val chronoWeeksFloat = chronologicalDays / 7.0f

        val pmaWeeksFloat = gestationalAgeAtBirthWeeks + chronoWeeksFloat

        val weeks = chronologicalDays / 7
        val remDays = chronologicalDays % 7
        val chronoDisplay = if (weeks == 0L) {
            "$chronologicalDays Hari"
        } else if (remDays == 0L) {
            "$weeks Minggu"
        } else {
            "$weeks Minggu $remDays Hari"
        }

        val pmaTotalDays = (gestationalAgeAtBirthWeeks * 7) + chronologicalDays
        val pmaWeeksPart = pmaTotalDays / 7
        val pmaDaysPart = pmaTotalDays % 7
        val pmaDisplay = if (pmaDaysPart == 0L) {
            "$pmaWeeksPart Minggu"
        } else {
            "$pmaWeeksPart Minggu $pmaDaysPart Hari"
        }

        return AgeCalculation(
            chronologicalDays = chronologicalDays,
            chronologicalWeeks = chronoWeeksFloat,
            pmaWeeks = pmaWeeksFloat,
            chronologicalDisplay = chronoDisplay,
            pmaDisplay = pmaDisplay
        )
    }
}
