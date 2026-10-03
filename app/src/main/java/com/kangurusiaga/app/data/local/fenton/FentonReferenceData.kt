package com.kangurusiaga.app.data.local.fenton

import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.util.FentonCalculator

/**
 * Fenton 2013 Preterm Growth Curves Reference Data & Calculations
 * Dynamically powered by Cole's LMS Formula:
 *   Z = ((X / M)^L - 1) / (L * S)  [jika L != 0]
 *   X = M * (1 + L * S * Z)^(1 / L) [jika L != 0]
 */
data class FentonCurvePoint(
    val pmaWeeks: Float,
    val p3: Float,
    val p10: Float,
    val p50: Float,
    val p90: Float,
    val p97: Float
)

data class GrowthPercentileEvaluation(
    val percentileBadge: String,
    val statusText: String,
    val isNormal: Boolean,
    val differenceFromMedian: Float,
    val medianValue: Float,
    val zScore: Double = 0.0,
    val clinicalClassification: String = "AGA"
)

data class LmsParameters(val week: Int, val l: Double, val m: Double, val s: Double)

object FentonReferenceData {

    const val MIN_PMA_WEEKS = 22f
    const val MAX_PMA_WEEKS = 50f

    // Koreksi smoothing pada W22 & W23 agar transisi menuju W24 mulus
    val LMS_FEMALE_HEAD_CIRCUMFERENCE: List<LmsParameters> = listOf(
        LmsParameters(22, 1.0, 19.55, 0.05749617),
        LmsParameters(23, 1.0, 20.45, 0.0565786),
        LmsParameters(24, 1.0, 21.34116222, 0.05655032),
        LmsParameters(25, 1.0, 22.26957061, 0.05631412),
        LmsParameters(26, 1.0, 23.1999059, 0.05599004),
        LmsParameters(27, 1.0, 24.13246205, 0.05556468),
        LmsParameters(28, 1.0, 25.06767785, 0.05501802),
        LmsParameters(29, 1.0, 26.00595327, 0.05433182),
        LmsParameters(30, 1.0, 26.94769871, 0.05348737),
        LmsParameters(31, 1.0, 27.8929842, 0.05246716),
        LmsParameters(32, 1.0, 28.83411671, 0.05127841),
        LmsParameters(33, 1.0, 29.7556392, 0.04995312),
        LmsParameters(34, 1.0, 30.64175716, 0.04852437),
        LmsParameters(35, 1.0, 31.47667602, 0.04702524),
        LmsParameters(36, 1.0, 32.24510352, 0.04548879),
        LmsParameters(37, 1.0, 32.94329966, 0.04394812),
        LmsParameters(38, 1.0, 33.5790767, 0.04243629),
        LmsParameters(39, 1.0, 34.1607492, 0.04098639),
        LmsParameters(40, 1.0, 34.6966317, 0.03963149),
        LmsParameters(41, 1.0, 35.19493494, 0.03840367),
        LmsParameters(42, 1.0, 35.6614821, 0.03731226),
        LmsParameters(43, 1.0, 36.09970881, 0.03634376),
        LmsParameters(44, 1.0, 36.51294686, 0.03548373),
        LmsParameters(45, 1.0, 36.9045282, 0.03471769),
        LmsParameters(46, 1.0, 37.27778411, 0.0340312),
        LmsParameters(47, 1.0, 37.63604844, 0.03340978),
        LmsParameters(48, 1.0, 37.98264541, 0.032839),
        LmsParameters(49, 1.0, 38.32093526, 0.03230428),
        LmsParameters(50, 1.0, 38.65419574, 0.03179139),
    )

    // Koreksi smoothing pada W22 & W23
    val LMS_MALE_HEAD_CIRCUMFERENCE: List<LmsParameters> = listOf(
        LmsParameters(22, 1.0, 19.80, 0.06002347),
        LmsParameters(23, 1.0, 20.75, 0.05767068),
        LmsParameters(24, 1.0, 21.74744252, 0.05756756),
        LmsParameters(25, 1.0, 22.72648153, 0.05683092),
        LmsParameters(26, 1.0, 23.70067113, 0.05606028),
        LmsParameters(27, 1.0, 24.66927146, 0.05525047),
        LmsParameters(28, 1.0, 25.63117825, 0.05439375),
        LmsParameters(29, 1.0, 26.58538485, 0.05348305),
        LmsParameters(30, 1.0, 27.53085847, 0.05251115),
        LmsParameters(31, 1.0, 28.46634433, 0.05147125),
        LmsParameters(32, 1.0, 29.38531931, 0.05036575),
        LmsParameters(33, 1.0, 30.27599432, 0.04920625),
        LmsParameters(34, 1.0, 31.12635114, 0.04800475),
        LmsParameters(35, 1.0, 31.92437159, 0.04677325),
        LmsParameters(36, 1.0, 32.65849659, 0.04552375),
        LmsParameters(37, 1.0, 33.32772614, 0.04426825),
        LmsParameters(38, 1.0, 33.94161932, 0.04301875),
        LmsParameters(39, 1.0, 34.51019432, 0.04178725),
        LmsParameters(40, 1.0, 35.04346932, 0.04058575),
        LmsParameters(41, 1.0, 35.55129602, 0.0394259),
        LmsParameters(42, 1.0, 36.03969716, 0.0383113),
        LmsParameters(43, 1.0, 36.51086648, 0.0372375),
        LmsParameters(44, 1.0, 36.96683124, 0.0361997),
        LmsParameters(45, 1.0, 37.40961879, 0.0351931),
        LmsParameters(46, 1.0, 37.84125609, 0.0342129),
        LmsParameters(47, 1.0, 38.26377163, 0.0332543),
        LmsParameters(48, 1.0, 38.67918807, 0.0323125),
        LmsParameters(49, 1.0, 39.08954996, 0.03138268),
        LmsParameters(50, 1.0, 39.4968517, 0.0304601),
    )

    // Koreksi smoothing pada W22 & W23
    val LMS_FEMALE_LENGTH: List<LmsParameters> = listOf(
        LmsParameters(22, 1.0, 27.60, 0.06358042),
        LmsParameters(23, 1.0, 28.95, 0.06156383),
        LmsParameters(24, 1.0, 30.26179151, 0.06181091),
        LmsParameters(25, 1.0, 31.60500091, 0.06342126),
        LmsParameters(26, 1.0, 32.95507245, 0.0647578),
        LmsParameters(27, 1.0, 34.31305304, 0.06577878),
        LmsParameters(28, 1.0, 35.68050529, 0.06642183),
        LmsParameters(29, 1.0, 37.05885364, 0.06663014),
        LmsParameters(30, 1.0, 38.44955956, 0.06634537),
        LmsParameters(31, 1.0, 39.85357032, 0.06551275),
        LmsParameters(32, 1.0, 41.26023785, 0.06414925),
        LmsParameters(33, 1.0, 42.64731534, 0.06234375),
        LmsParameters(34, 1.0, 43.99205193, 0.06018825),
        LmsParameters(35, 1.0, 45.2716967, 0.05777475),
        LmsParameters(36, 1.0, 46.4642392, 0.05519525),
        LmsParameters(37, 1.0, 47.56469943, 0.05254175),
        LmsParameters(38, 1.0, 48.58512784, 0.04990625),
        LmsParameters(39, 1.0, 49.53831534, 0.04738075),
        LmsParameters(40, 1.0, 50.43705284, 0.04505725),
        LmsParameters(41, 1.0, 51.29393824, 0.04302512),
        LmsParameters(42, 1.0, 52.11713017, 0.04131337),
        LmsParameters(43, 1.0, 52.91034802, 0.03989062),
        LmsParameters(44, 1.0, 53.6771181, 0.03872287),
        LmsParameters(45, 1.0, 54.42096695, 0.03777612),
        LmsParameters(46, 1.0, 55.14542034, 0.03701638),
        LmsParameters(47, 1.0, 55.8540068, 0.03640961),
        LmsParameters(48, 1.0, 56.55024462, 0.03592191),
        LmsParameters(49, 1.0, 57.23769028, 0.03551898),
        LmsParameters(50, 1.0, 57.9198129, 0.03516738),
    )

    // Koreksi smoothing pada W22 & W23
    val LMS_MALE_LENGTH: List<LmsParameters> = listOf(
        LmsParameters(22, 1.0, 28.10, 0.06526188),
        LmsParameters(23, 1.0, 29.50, 0.06356677),
        LmsParameters(24, 1.0, 30.92383772, 0.06384423),
        LmsParameters(25, 1.0, 32.3173016, 0.06546415),
        LmsParameters(26, 1.0, 33.70004573, 0.06634974),
        LmsParameters(27, 1.0, 35.0744484, 0.06652236),
        LmsParameters(28, 1.0, 36.44594068, 0.06607633),
        LmsParameters(29, 1.0, 37.82034121, 0.06512651),
        LmsParameters(30, 1.0, 39.20340988, 0.06378374),
        LmsParameters(31, 1.0, 40.60029911, 0.06215858),
        LmsParameters(32, 1.0, 42.00182276, 0.06032978),
        LmsParameters(33, 1.0, 43.38446154, 0.05834471),
        LmsParameters(34, 1.0, 44.72407261, 0.05624933),
        LmsParameters(35, 1.0, 45.99651323, 0.05408962),
        LmsParameters(36, 1.0, 47.17853523, 0.05191142),
        LmsParameters(37, 1.0, 48.26746662, 0.04975794),
        LmsParameters(38, 1.0, 49.28121154, 0.04766971),
        LmsParameters(39, 1.0, 50.23856877, 0.04568718),
        LmsParameters(40, 1.0, 51.15833708, 0.04385077),
        LmsParameters(41, 1.0, 52.05885, 0.04219975),
        LmsParameters(42, 1.0, 52.94774077, 0.04074648),
        LmsParameters(43, 1.0, 53.82194232, 0.03947644),
        LmsParameters(44, 1.0, 54.67792228, 0.03837394),
        LmsParameters(45, 1.0, 55.51214858, 0.03742329),
        LmsParameters(46, 1.0, 56.32127433, 0.03660858),
        LmsParameters(47, 1.0, 57.10624193, 0.03590914),
        LmsParameters(48, 1.0, 57.87226316, 0.03529955),
        LmsParameters(49, 1.0, 58.62479465, 0.03475394),
        LmsParameters(50, 1.0, 59.36915908, 0.03424707),
    )

    val LMS_FEMALE_WEIGHT: List<LmsParameters> = listOf(
        LmsParameters(22, 0.09095614, 512.9417451, 0.14175331),
        LmsParameters(23, 0.30371555, 554.5536099, 0.14998405),
        LmsParameters(24, 0.52944866, 606.23788, 0.16135078),
        LmsParameters(25, 0.82750244, 694.1299374, 0.18059744),
        LmsParameters(26, 1.05097644, 791.8479483, 0.19867479),
        LmsParameters(27, 1.19154509, 898.5086868, 0.21331501),
        LmsParameters(28, 1.2591772, 1017.2971, 0.2239227),
        LmsParameters(29, 1.26456372, 1151.840087, 0.2300789),
        LmsParameters(30, 1.21839557, 1305.703411, 0.23134073),
        LmsParameters(31, 1.13136368, 1482.294034, 0.22731548),
        LmsParameters(32, 1.01415898, 1680.982739, 0.21861497),
        LmsParameters(33, 0.87747241, 1897.111553, 0.20685778),
        LmsParameters(34, 0.7319949, 2125.841433, 0.19370609),
        LmsParameters(35, 0.58841738, 2362.35385, 0.18082214),
        LmsParameters(36, 0.45743079, 2601.593015, 0.1698241),
        LmsParameters(37, 0.34942639, 2835.084551, 0.16131757),
        LmsParameters(38, 0.26790355, 3049.66826, 0.15489552),
        LmsParameters(39, 0.20946967, 3239.306012, 0.1501069),
        LmsParameters(40, 0.17043252, 3415.268552, 0.14650067),
        LmsParameters(41, 0.14709983, 3595.664661, 0.14363313),
        LmsParameters(42, 0.1357795, 3787.384395, 0.14122975),
        LmsParameters(43, 0.13277872, 3987.366391, 0.13918514),
        LmsParameters(44, 0.13440746, 4192.027788, 0.13740129),
        LmsParameters(45, 0.13696519, 4397.806981, 0.13578017),
        LmsParameters(46, 0.13679063, 4601.193312, 0.13422577),
        LmsParameters(47, 0.130076, 4800.057931, 0.13268828),
        LmsParameters(48, 0.11356027, 4993.597322, 0.13116417),
        LmsParameters(49, 0.08354607, 5181.228351, 0.12965172),
        LmsParameters(50, 0.0779, 5362.0, 0.12814963),
    )

    val LMS_MALE_WEIGHT: List<LmsParameters> = listOf(
        LmsParameters(22, 0.67493508, 538.3337779, 0.13814013),
        LmsParameters(23, 0.79707872, 592.5751, 0.15053941),
        LmsParameters(24, 0.91320459, 651.0479355, 0.16230485),
        LmsParameters(25, 1.0614997, 741.355579, 0.17727229),
        LmsParameters(26, 1.18991812, 841.1864045, 0.19014618),
        LmsParameters(27, 1.29378108, 952.6016988, 0.20043372),
        LmsParameters(28, 1.36869215, 1078.745738, 0.20767184),
        LmsParameters(29, 1.41017929, 1222.894209, 0.21139984),
        LmsParameters(30, 1.4137907, 1388.303356, 0.21139659),
        LmsParameters(31, 1.37541167, 1578.021161, 0.20769714),
        LmsParameters(32, 1.29880701, 1790.184316, 0.20079173),
        LmsParameters(33, 1.19561916, 2018.019995, 0.19161523),
        LmsParameters(34, 1.07783318, 2254.541798, 0.18112185),
        LmsParameters(35, 0.95743411, 2492.763349, 0.17026582),
        LmsParameters(36, 0.8461229, 2725.69827, 0.15998463),
        LmsParameters(37, 0.74906589, 2946.660647, 0.1508313),
        LmsParameters(38, 0.66489486, 3155.875301, 0.14297436),
        LmsParameters(39, 0.59195748, 3360.477788, 0.13656562),
        LmsParameters(40, 0.5286014, 3567.904128, 0.13175691),
        LmsParameters(41, 0.47319673, 3785.348823, 0.12868534),
        LmsParameters(42, 0.42462944, 4014.451338, 0.12715008),
        LmsParameters(43, 0.3823014, 4251.296186, 0.12661234),
        LmsParameters(44, 0.34563692, 4491.726109, 0.12651868),
        LmsParameters(45, 0.31406026, 4731.584772, 0.1263155),
        LmsParameters(46, 0.28699026, 4966.79223, 0.12547129),
        LmsParameters(47, 0.2637161, 5195.117581, 0.12394836),
        LmsParameters(48, 0.24340048, 5416.118152, 0.12221192),
        LmsParameters(49, 0.22519132, 5629.61009, 0.12072216),
        LmsParameters(50, 0.20825748, 5835.0, 0.12),
    )

    private fun generateCurvePoints(lmsList: List<LmsParameters>, isWeight: Boolean): List<FentonCurvePoint> {
        val divisor = if (isWeight) 1000.0 else 1.0
        return lmsList.map { p ->
            val p3 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P3, p.l, p.m, p.s) / divisor
            val p10 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P10, p.l, p.m, p.s) / divisor
            val p50 = p.m / divisor
            val p90 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P90, p.l, p.m, p.s) / divisor
            val p97 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P97, p.l, p.m, p.s) / divisor

            FentonCurvePoint(
                pmaWeeks = p.week.toFloat(),
                p3 = p3.toFloat(),
                p10 = p10.toFloat(),
                p50 = p50.toFloat(),
                p90 = p90.toFloat(),
                p97 = p97.toFloat()
            )
        }
    }

    val FEMALE_WEIGHT: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_FEMALE_WEIGHT, true) }
    val MALE_WEIGHT: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_MALE_WEIGHT, true) }
    val FEMALE_LENGTH: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_FEMALE_LENGTH, false) }
    val MALE_LENGTH: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_MALE_LENGTH, false) }
    val FEMALE_HC: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_FEMALE_HEAD_CIRCUMFERENCE, false) }
    val MALE_HEAD_CIRCUMFERENCE: List<FentonCurvePoint> by lazy { generateCurvePoints(LMS_MALE_HEAD_CIRCUMFERENCE, false) }

    fun getCurvePoints(parameter: GrowthParameter, gender: Gender?): List<FentonCurvePoint> {
        val isMale = gender != Gender.FEMALE
        return when (parameter) {
            GrowthParameter.WEIGHT -> if (isMale) MALE_WEIGHT else FEMALE_WEIGHT
            GrowthParameter.LENGTH -> if (isMale) MALE_LENGTH else FEMALE_LENGTH
            GrowthParameter.HEAD_CIRCUMFERENCE -> if (isMale) MALE_HEAD_CIRCUMFERENCE else FEMALE_HC
        }
    }

    fun getLmsParameters(parameter: GrowthParameter, gender: Gender?): List<LmsParameters> {
        val isMale = gender != Gender.FEMALE
        return when (parameter) {
            GrowthParameter.WEIGHT -> if (isMale) LMS_MALE_WEIGHT else LMS_FEMALE_WEIGHT
            GrowthParameter.LENGTH -> if (isMale) LMS_MALE_LENGTH else LMS_FEMALE_LENGTH
            GrowthParameter.HEAD_CIRCUMFERENCE -> if (isMale) LMS_MALE_HEAD_CIRCUMFERENCE else LMS_FEMALE_HEAD_CIRCUMFERENCE
        }
    }

    /**
     * Mengambil parameter LMS dengan interpolasi linier presisi jika pmaWeeks berupa pecahan (desimal).
     * Mencegah lompatan/kinking pada evaluasi klinis pasien.
     */
    fun getInterpolatedLms(parameter: GrowthParameter, gender: Gender?, pmaWeeks: Float): LmsParameters {
        val list = getLmsParameters(parameter, gender)
        val clampedPma = pmaWeeks.coerceIn(MIN_PMA_WEEKS, MAX_PMA_WEEKS)
        val lowerWeek = clampedPma.toInt().coerceIn(22, 50)
        val upperWeek = (lowerWeek + 1).coerceAtMost(50)

        val lower = list.firstOrNull { it.week == lowerWeek } ?: list.first()
        if (lowerWeek == upperWeek || clampedPma == lowerWeek.toFloat()) return lower

        val upper = list.firstOrNull { it.week == upperWeek } ?: list.last()
        val fraction = (clampedPma - lowerWeek) / (upperWeek - lowerWeek).toDouble()

        return LmsParameters(
            week = lowerWeek,
            l = lower.l + fraction * (upper.l - lower.l),
            m = lower.m + fraction * (upper.m - lower.m),
            s = lower.s + fraction * (upper.s - lower.s)
        )
    }

    fun interpolatePoint(parameter: GrowthParameter, gender: Gender?, pmaWeeks: Float): FentonCurvePoint {
        val curve = getCurvePoints(parameter, gender)
        val clampedPma = pmaWeeks.coerceIn(MIN_PMA_WEEKS, MAX_PMA_WEEKS)
        val lower = curve.lastOrNull { it.pmaWeeks <= clampedPma } ?: curve.first()
        val upper = curve.firstOrNull { it.pmaWeeks >= clampedPma } ?: curve.last()

        if (lower.pmaWeeks == upper.pmaWeeks) return lower

        val fraction = (clampedPma - lower.pmaWeeks) / (upper.pmaWeeks - lower.pmaWeeks)
        return FentonCurvePoint(
            pmaWeeks = clampedPma,
            p3 = lower.p3 + fraction * (upper.p3 - lower.p3),
            p10 = lower.p10 + fraction * (upper.p10 - lower.p10),
            p50 = lower.p50 + fraction * (upper.p50 - lower.p50),
            p90 = lower.p90 + fraction * (upper.p90 - lower.p90),
            p97 = lower.p97 + fraction * (upper.p97 - lower.p97)
        )
    }

    fun evaluatePercentile(
        parameter: GrowthParameter,
        gender: Gender?,
        pmaWeeks: Float,
        value: Float
    ): GrowthPercentileEvaluation {
        // 1. Menggunakan LMS terinterpolasi agar perhitungan Z-score sinkron dengan titik pada grafik
        val lms = getInterpolatedLms(parameter, gender, pmaWeeks)
        val isWeight = parameter == GrowthParameter.WEIGHT

        // 2. Normalisasi input: deteksi apakah input berat dalam kilogram (< 100) atau gram
        val isInputInKg = isWeight && value < 100f
        val xGramsOrCm = if (isInputInKg) value.toDouble() * 1000.0 else value.toDouble()

        val zScore = FentonCalculator.calculateZScore(xGramsOrCm, lms.l, lms.m, lms.s)

        val badge = FentonCalculator.getPercentileBadge(zScore)
        val statusText = FentonCalculator.getStatusDescription(zScore)
        val classification = FentonCalculator.getClinicalClassification(zScore)
        val isNormal = classification == "AGA"

        // 3. Menyamakan satuan medianDisplay dengan satuan nilai input (value)
        val medianDisplay = if (isInputInKg) {
            (lms.m / 1000.0).toFloat()
        } else {
            lms.m.toFloat()
        }

        // 4. Selisih dari median sekarang berada pada satuan yang identik
        val diffFromMedian = value - medianDisplay

        return GrowthPercentileEvaluation(
            percentileBadge = badge,
            statusText = statusText,
            isNormal = isNormal,
            differenceFromMedian = diffFromMedian,
            medianValue = medianDisplay,
            zScore = zScore,
            clinicalClassification = classification
        )
    }
}