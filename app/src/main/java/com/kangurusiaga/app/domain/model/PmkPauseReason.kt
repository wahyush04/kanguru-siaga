package com.kangurusiaga.app.domain.model

enum class PmkPauseReason(
    val title: String,
    val iconEmoji: String,
    val description: String
) {
    UNSTABLE_CONDITION(
        title = "Kondisi bayi tidak stabil/gawat",
        iconEmoji = "⚠️",
        description = "Kondisi klinis bayi tidak stabil atau tanda gawat darurat"
    ),
    MEDICAL_CHECKUP(
        title = "Pemeriksaan kesehatan utk bayi",
        iconEmoji = "🩺",
        description = "Pemeriksaan medis atau tindakan evaluasi kesehatan bayi"
    ),
    CAREGIVER_NEED(
        title = "Ibu/pendamping ada Keperluan lain",
        iconEmoji = "👥",
        description = "Ibu atau pendamping ada keperluan lain"
    );

    companion object {
        fun fromString(value: String?): PmkPauseReason? {
            if (value == null) return null
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.title.equals(value, ignoreCase = true)
            } ?: when {
                value.contains("stabil", ignoreCase = true) || value.contains("gawat", ignoreCase = true) -> UNSTABLE_CONDITION
                value.contains("periksa", ignoreCase = true) || value.contains("medis", ignoreCase = true) -> MEDICAL_CHECKUP
                value.contains("keperluan", ignoreCase = true) || value.contains("ibu", ignoreCase = true) || value.contains("rehat", ignoreCase = true) -> CAREGIVER_NEED
                else -> CAREGIVER_NEED
            }
        }
    }
}
