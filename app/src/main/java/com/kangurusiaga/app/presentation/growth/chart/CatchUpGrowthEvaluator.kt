package com.kangurusiaga.app.presentation.growth.chart

import java.util.Locale
import kotlin.math.round

enum class GrowthEvaluationLevel {
    OPTIMAL,   // Emerald/Green (Sesuai Target, Sangat Baik)
    NORMAL,    // Neutral/Slate (Normal, Proporsional)
    WARNING,   // Amber/Yellow (Kurang Optimal, Mendatar, Perlu Waspada, Perlu Evaluasi)
    DANGER,    // Red/Rose (Menurun, Penurunan Berat)
    BASELINE   // Neutral (Data Dasar)
}

data class MetricEvaluationItem(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val level: GrowthEvaluationLevel
)

data class CatchUpGrowthSummary(
    val overallLevel: GrowthEvaluationLevel,
    val weightItem: MetricEvaluationItem,
    val lengthItem: MetricEvaluationItem,
    val headCircItem: MetricEvaluationItem,
    val clinicalNoteTitle: String,
    val clinicalNoteMessage: String
)

object CatchUpGrowthEvaluator {

    fun evaluate(
        weightKg: Float?,
        weightBadge: String,
        weightGainRateGramsPerDay: Float?,
        lengthCm: Float?,
        lengthBadge: String,
        lengthGainRateCmPerWeek: Float?,
        headCircCm: Float?,
        headCircBadge: String,
        headCircGainRateCmPerWeek: Float?
    ): CatchUpGrowthSummary {
        val cleanWeightBadge = sanitizeBadge(weightBadge)
        val cleanLengthBadge = sanitizeBadge(lengthBadge)
        val cleanHeadBadge = sanitizeBadge(headCircBadge)

        val weightItem = evaluateWeight(weightKg, cleanWeightBadge, weightGainRateGramsPerDay)
        val lengthItem = evaluateLength(lengthCm, cleanLengthBadge, lengthGainRateCmPerWeek)
        val headCircItem = evaluateHeadCirc(headCircCm, cleanHeadBadge, headCircGainRateCmPerWeek)

        val overallLevel = determineOverallLevel(weightItem.level, lengthItem.level, headCircItem.level)
        val (noteTitle, noteMessage) = generateClinicalNote(overallLevel, weightItem, headCircItem)

        return CatchUpGrowthSummary(
            overallLevel = overallLevel,
            weightItem = weightItem,
            lengthItem = lengthItem,
            headCircItem = headCircItem,
            clinicalNoteTitle = noteTitle,
            clinicalNoteMessage = noteMessage
        )
    }

    private fun sanitizeBadge(raw: String): String {
        return if (raw.isBlank() || raw == "-") "-" else raw
    }

    private fun evaluateWeight(
        weightKg: Float?,
        badge: String,
        gainRate: Float?
    ): MetricEvaluationItem {
        if (weightKg == null) {
            return MetricEvaluationItem(
                title = "Berat Badan: -",
                subtitle = "Belum ada pengukuran berat badan",
                badgeText = "-",
                level = GrowthEvaluationLevel.BASELINE
            )
        }

        val formattedGrams = String.format(Locale("id", "ID"), "%,d g", (weightKg * 1000f).toInt())
        val title = "Berat Badan: $formattedGrams"

        if (gainRate == null) {
            return MetricEvaluationItem(
                title = title,
                subtitle = "Persentil $badge • Pengukuran pertama (data dasar)",
                badgeText = "Data Dasar",
                level = GrowthEvaluationLevel.BASELINE
            )
        }

        val roundedRate = round(gainRate).toInt()
        val sign = if (roundedRate > 0) "+" else ""

        return when {
            // Skenario 1: Pertumbuhan Menurun / Penurunan Berat Badan
            gainRate < -0.1f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$roundedRate g/hari (terjadi penurunan)",
                    badgeText = "Menurun",
                    level = GrowthEvaluationLevel.DANGER
                )
            }
            // Skenario 2: Pertumbuhan Mendatar / Stagnan (0 - 0.9 g/hari)
            gainRate <= 0.9f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$roundedRate g/hari (kenaikan mendatar)",
                    badgeText = "Mendatar",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario 3: Pertumbuhan Kurang Optimal (< 15 g/hari)
            gainRate < 15.0f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju kenaikan $sign$roundedRate g/hari (di bawah target)",
                    badgeText = "Kurang Optimal",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario 4: Pertumbuhan Sangat Pesat (> 35 g/hari)
            gainRate > 35.0f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju kenaikan $sign$roundedRate g/hari (kejar tumbuh pesat)",
                    badgeText = "Sesuai Target",
                    level = GrowthEvaluationLevel.OPTIMAL
                )
            }
            // Skenario 5: Pertumbuhan Ideal / Sesuai Target (15 - 35 g/hari)
            else -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju kenaikan $sign$roundedRate g/hari",
                    badgeText = "Sesuai Target",
                    level = GrowthEvaluationLevel.OPTIMAL
                )
            }
        }
    }

    private fun evaluateLength(
        lengthCm: Float?,
        badge: String,
        gainRate: Float?
    ): MetricEvaluationItem {
        if (lengthCm == null) {
            return MetricEvaluationItem(
                title = "Panjang Badan: -",
                subtitle = "Belum ada pengukuran panjang badan",
                badgeText = "-",
                level = GrowthEvaluationLevel.BASELINE
            )
        }

        val formattedLength = String.format(Locale.US, "%.1f cm", lengthCm)
        val title = "Panjang Badan: $formattedLength"

        if (gainRate == null) {
            return MetricEvaluationItem(
                title = title,
                subtitle = "Persentil $badge • Pertumbuhan proporsional",
                badgeText = "Normal",
                level = GrowthEvaluationLevel.NORMAL
            )
        }

        val formattedRate = String.format(Locale.US, "%.1f", gainRate)
        val sign = if (gainRate > 0) "+" else ""

        return when {
            // Skenario: Penurunan cm (kemungkinan galat ukur)
            gainRate < -0.1f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$formattedRate cm/mgg (indikasi galat ukur)",
                    badgeText = "Perlu Evaluasi",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario: Mendatar (< 0.4 cm/minggu)
            gainRate < 0.4f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$formattedRate cm/mgg (pertumbuhan mendatar)",
                    badgeText = "Mendatar",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario: Sangat Baik (> 1.4 cm/minggu)
            gainRate > 1.4f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$formattedRate cm/mgg (kejar tumbuh pesat)",
                    badgeText = "Sangat Baik",
                    level = GrowthEvaluationLevel.OPTIMAL
                )
            }
            // Skenario: Normal / Proporsional (0.4 - 1.4 cm/minggu)
            else -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Pertumbuhan proporsional",
                    badgeText = "Normal",
                    level = GrowthEvaluationLevel.NORMAL
                )
            }
        }
    }

    private fun evaluateHeadCirc(
        headCircCm: Float?,
        badge: String,
        gainRate: Float?
    ): MetricEvaluationItem {
        if (headCircCm == null) {
            return MetricEvaluationItem(
                title = "Lingkar Kepala: -",
                subtitle = "Belum ada pengukuran lingkar kepala",
                badgeText = "-",
                level = GrowthEvaluationLevel.BASELINE
            )
        }

        val formattedHead = String.format(Locale.US, "%.1f cm", headCircCm)
        val title = "Lingkar Kepala: $formattedHead"

        if (gainRate == null) {
            return MetricEvaluationItem(
                title = title,
                subtitle = "Persentil $badge • Perkembangan neurokranial baik",
                badgeText = "Normal",
                level = GrowthEvaluationLevel.NORMAL
            )
        }

        val formattedRate = String.format(Locale.US, "%.1f", gainRate)
        val sign = if (gainRate > 0) "+" else ""

        return when {
            // Skenario: Penurunan (kemungkinan galat ukur)
            gainRate < -0.1f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$formattedRate cm/mgg (indikasi galat ukur)",
                    badgeText = "Perlu Evaluasi",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario: Kurang optimal / Mendatar (< 0.3 cm/minggu)
            gainRate < 0.3f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Laju $sign$formattedRate cm/mgg (pertumbuhan lambat)",
                    badgeText = "Kurang Optimal",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario: Lonjakan Cepat (> 1.1 cm/minggu)
            gainRate > 1.1f -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Lonjakan $sign$formattedRate cm/mgg (perlu evaluasi lingkar kepala)",
                    badgeText = "Perlu Waspada",
                    level = GrowthEvaluationLevel.WARNING
                )
            }
            // Skenario: Normal / Optimal (0.3 - 1.1 cm/minggu)
            else -> {
                MetricEvaluationItem(
                    title = title,
                    subtitle = "Persentil $badge • Perkembangan neurokranial baik",
                    badgeText = "Normal",
                    level = GrowthEvaluationLevel.NORMAL
                )
            }
        }
    }

    private fun determineOverallLevel(
        weightLevel: GrowthEvaluationLevel,
        lengthLevel: GrowthEvaluationLevel,
        headLevel: GrowthEvaluationLevel
    ): GrowthEvaluationLevel {
        return when {
            weightLevel == GrowthEvaluationLevel.DANGER -> GrowthEvaluationLevel.DANGER
            weightLevel == GrowthEvaluationLevel.WARNING || lengthLevel == GrowthEvaluationLevel.WARNING || headLevel == GrowthEvaluationLevel.WARNING -> GrowthEvaluationLevel.WARNING
            weightLevel == GrowthEvaluationLevel.OPTIMAL -> GrowthEvaluationLevel.OPTIMAL
            weightLevel == GrowthEvaluationLevel.BASELINE && lengthLevel == GrowthEvaluationLevel.BASELINE && headLevel == GrowthEvaluationLevel.BASELINE -> GrowthEvaluationLevel.BASELINE
            else -> GrowthEvaluationLevel.NORMAL
        }
    }

    private fun generateClinicalNote(
        overallLevel: GrowthEvaluationLevel,
        weightItem: MetricEvaluationItem,
        headCircItem: MetricEvaluationItem
    ): Pair<String, String> {
        return when (overallLevel) {
            GrowthEvaluationLevel.DANGER -> {
                Pair(
                    "Catatan Klinis: Penurunan Berat Badan (Perhatian Segera)",
                    "Terdeteksi adanya penurunan berat badan pada pengukuran terakhir. Segera konsultasikan dengan dokter spesialis anak untuk evaluasi asupan ASI/nutrisi, kemungkinan infeksi, toleransi saluran cerna, atau dehidrasi. Pertahankan kestabilan suhu tubuh dengan metode kanguru (PMK)."
                )
            }
            GrowthEvaluationLevel.WARNING -> {
                if (headCircItem.badgeText == "Perlu Waspada") {
                    Pair(
                        "Catatan Klinis: Evaluasi Laju Lingkar Kepala",
                        "Laju pertambahan lingkar kepala mengalami lonjakan di atas rata-rata (> 1,1 cm/minggu). Disarankan untuk berkonsultasi dengan dokter spesialis anak guna evaluasi neurosonografi (USG kepala) dan perabaan fontanel."
                    )
                } else if (weightItem.level == GrowthEvaluationLevel.WARNING) {
                    Pair(
                        "Catatan Klinis: Laju Pertumbuhan Belum Optimal",
                        "Kenaikan berat badan harian masih di bawah target catch-up growth prematur (minimal 15 g/hari). Evaluasi kembali frekuensi menyusu, volume ASI perah yang diberikan, teknik pelekatan, dan diskusikan potensi indikasi fortifikasi ASI dengan dokter anak."
                    )
                } else {
                    Pair(
                        "Catatan Klinis: Evaluasi Konsistensi Pengukuran",
                        "Terdapat fluktuasi pada pengukuran panjang badan atau lingkar kepala. Pastikan posisi bayi lurus saat pengukuran panjang dan pita ukur melingkari dahi serta oksiput dengan pas. Pantau kembali pada jadwal penimbangan berikutnya."
                    )
                }
            }
            GrowthEvaluationLevel.OPTIMAL -> {
                if (weightItem.subtitle.contains("pesat")) {
                    Pair(
                        "Catatan Klinis: Pertumbuhan Kejar Sangat Pesat",
                        "Laju kenaikan berat badan melampaui target rata-rata (> 35 g/hari). Kejar tumbuh berlangsung sangat baik. Tetap pantau proporsionalitas terhadap panjang badan dan lingkar kepala, serta konsultasikan kurva Fenton secara berkala."
                    )
                } else {
                    Pair(
                        "Catatan Klinis: Pertumbuhan Sesuai Target Ideal",
                        "Pertumbuhan kejar (catch-up growth) bayi prematur berjalan sangat baik sesuai kurva Fenton (target 15–35 g/hari). Lanjutkan pemberian ASI eksklusif/optimal, perawatan metode kanguru (PMK) secara konsisten, dan pemantauan berat badan berkala."
                    )
                }
            }
            GrowthEvaluationLevel.BASELINE -> {
                Pair(
                    "Catatan Klinis: Data Awal Pengukuran",
                    "Data dasar pertumbuhan bayi telah tercatat. Lakukan penimbangan dan pengukuran berkala (minimal 1–2 kali seminggu) untuk memantau laju kenaikan harian serta memetakan tren kurva pertumbuhan Fenton secara akurat."
                )
            }
            GrowthEvaluationLevel.NORMAL -> {
                Pair(
                    "Catatan Klinis: Pemantauan Pertumbuhan Berkala",
                    "Pertumbuhan bayi berada dalam rentang wajar. Pantau kurva pertumbuhan berkala sesuai usia pasca-menstruasi (PMA). Konsultasikan kecukupan nutrisi ASI dan pelaksanaan PMK secara teratur dengan dokter anak."
                )
            }
        }
    }
}

