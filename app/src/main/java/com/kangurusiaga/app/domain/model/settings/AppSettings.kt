package com.kangurusiaga.app.domain.model.settings

enum class AppThemeMode(val title: String, val subtitle: String) {
    WARM_LIGHT(
        title = "Terang Hangat",
        subtitle = "Warna krem pastel hangat yang lembut, menenangkan dan tidak silau bagi mata bayi saat kontak kulit (PMK)."
    ),
    DIM_NURSING(
        title = "Mode Redup (Ruang Menyusui)",
        subtitle = "Latar gelap lembut dengan kontras rendah, ideal digunakan saat memantau suhu, timer PMK, atau jadwal ASI malam hari."
    ),
    SYSTEM(
        title = "Otomatis (Sistem HP)",
        subtitle = "Secara otomatis beralih antara Terang Hangat di siang hari dan Mode Redup pada malam hari mengikuti setelan perangkat Anda."
    );

    companion object {
        fun fromString(value: String?): AppThemeMode {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: WARM_LIGHT
        }
    }
}

enum class AppTextScale(
    val scaleFactor: Float,
    val percentageLabel: String,
    val title: String,
    val description: String
) {
    SMALL(
        scaleFactor = 0.90f,
        percentageLabel = "90%",
        title = "Kecil",
        description = "Memuat lebih banyak catatan dan grafik parameter dalam 1 layar."
    ),
    STANDARD(
        scaleFactor = 1.00f,
        percentageLabel = "100%",
        title = "Sedang (Standar)",
        description = "Rasio proporsional panduan klinis Kemenkes & IDAI, nyaman sehari-hari."
    ),
    LARGE(
        scaleFactor = 1.15f,
        percentageLabel = "115%",
        title = "Besar",
        description = "Teks lebih tegas dan sangat jelas saat posisi menggendong / PMK."
    ),
    EXTRA_LARGE(
        scaleFactor = 1.30f,
        percentageLabel = "130%",
        title = "Sangat Besar",
        description = "Keterbacaan maksimal untuk kemudahan membaca tanpa kacamata."
    );

    companion object {
        fun fromScale(scale: Float?): AppTextScale {
            if (scale == null) return STANDARD
            return entries.minByOrNull { kotlin.math.abs(it.scaleFactor - scale) } ?: STANDARD
        }

        fun fromString(value: String?): AppTextScale {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: STANDARD
        }
    }
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.WARM_LIGHT,
    val textScale: AppTextScale = AppTextScale.STANDARD,
    val notificationsEnabled: Boolean = true
)
