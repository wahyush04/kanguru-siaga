package com.kangurusiaga.app.domain.util

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Mathematical implementation of Cole's LMS Method for Fenton 2013 Preterm Growth Curves.
 *
 * Formulas:
 * 1. Z-Score calculation:
 *    Z = ((X / M)^L - 1) / (L * S)     when L != 0
 *    Z = ln(X / M) / S                 when L == 0
 *
 * 2. Value calculation from Z-Score:
 *    X = M * (1 + L * S * Z)^(1 / L)   when L != 0
 *    X = M * exp(S * Z)                when L == 0
 *
 * Standard Percentile Z values:
 *    P3  : Z = -1.88079
 *    P10 : Z = -1.28155
 *    P50 : Z =  0.00000 (X = M)
 *    P90 : Z =  1.28155
 *    P97 : Z =  1.88079
 *
 * Clinical Classifications:
 *    SGA : Z < -1.28155 (Below P10)
 *    AGA : -1.28155 <= Z <= 1.28155 (P10 to P90)
 *    LGA : Z > 1.28155 (Above P90)
 */
object FentonCalculator {

    const val Z_P3 = -1.88079
    const val Z_P10 = -1.28155
    const val Z_P50 = 0.0
    const val Z_P90 = 1.28155
    const val Z_P97 = 1.88079

    private const val EPSILON = 1e-6

    /**
     * Calculates the Z-score for a given measurement [x] based on LMS parameters [l], [m], and [s].
     */
    fun calculateZScore(x: Double, l: Double, m: Double, s: Double): Double {
        if (x <= 0.0 || m <= 0.0 || s <= 0.0) return 0.0

        val ratio = x / m
        return if (abs(l) > EPSILON) {
            (ratio.pow(l) - 1.0) / (l * s)
        } else {
            ln(ratio) / s
        }
    }

    /**
     * Calculates the centile value [x] corresponding to a given [zScore] and LMS parameters [l], [m], and [s].
     */
    fun calculateValueFromZ(zScore: Double, l: Double, m: Double, s: Double): Double {
        if (m <= 0.0 || s <= 0.0) return 0.0

        return if (abs(l) > EPSILON) {
            val base = 1.0 + l * s * zScore
            if (base <= 0.0) 0.0 else m * base.pow(1.0 / l)
        } else {
            m * exp(s * zScore)
        }
    }

    /**
     * Determines the clinical classification ("SGA", "AGA", "LGA") based on Z-score.
     */
    fun getClinicalClassification(zScore: Double): String {
        return when {
            zScore < Z_P10 -> "SGA" // Small for Gestational Age (< P10)
            zScore > Z_P90 -> "LGA" // Large for Gestational Age (> P90)
            else -> "AGA"           // Appropriate for Gestational Age (P10 - P90)
        }
    }

    /**
     * Returns a user-friendly badge for the percentile (e.g. "< P3", "P3 - P10", "P50", "P90 - P97", "> P97").
     */
    fun getPercentileBadge(zScore: Double): String {
        return when {
            zScore < Z_P3 -> "< P3"
            zScore < Z_P10 -> "P3 - P10"
            zScore in -0.1..0.1 -> "P50"
            zScore < 0.0 -> "P10 - P50"
            zScore <= Z_P90 -> "P50 - P90"
            zScore <= Z_P97 -> "P90 - P97"
            else -> "> P97"
        }
    }

    /**
     * Returns human-readable Indonesian clinical interpretation text.
     */
    fun getStatusDescription(zScore: Double): String {
        return when {
            zScore < Z_P3 -> "Di bawah persentil 3"
            zScore < Z_P10 -> "Kecil untuk Masa Kehamilan (KMK / SGA)"
            zScore <= Z_P90 -> "Sesuai Masa Kehamilan (SMK / AGA)"
            zScore <= Z_P97 -> "Di atas rata-rata"
            else -> "Besar untuk Masa Kehamilan (BMK / LGA)"
        }
    }

    /**
     * Calculates the cumulative percentile percentage (0.01% - 99.99%) from a Z-score
     * using the standard normal cumulative distribution function (CDF).
     */
    fun zScoreToPercentilePercentage(zScore: Double): Double {
        val cdf = 0.5 * (1.0 + erf(zScore / sqrt(2.0)))
        return (cdf * 100.0).coerceIn(0.01, 99.99)
    }

    /**
     * High precision Error function (erf) approximation (Abramowitz & Stegun formula 7.1.26).
     * Maximum error: 1.5e-7
     */
    fun erf(x: Double): Double {
        val sign = if (x >= 0) 1.0 else -1.0
        val absX = abs(x)

        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911

        val t = 1.0 / (1.0 + p * absX)
        val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * exp(-absX * absX)
        return sign * y
    }
}
