package com.kangurusiaga.app.domain.model

enum class PmkPauseReason(
    val title: String,
    val iconEmoji: String,
    val description: String
) {
    NURSING("Menyusu Langsung", "🍼", "Direct Latch / Pemberian ASI"),
    DIAPER("Ganti Popok & Bersihkan", "👶", "Pergantian popok atau pakaian basah"),
    BATHING("Mandi / Lap Bayi", "🛁", "Mandi air hangat atau seka tubuh singkat"),
    CAREGIVER_MEAL("Ibu / Pengasuh Makan & Rehat", "🍽️", "Kebutuhan makan, minum, atau ke toilet"),
    MEDICAL_OTHER("Pemeriksaan / Lainnya", "🩺", "Pemeriksaan medis nakes atau kebutuhan penting lainnya");

    companion object {
        fun fromString(value: String?): PmkPauseReason? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.title.equals(value, ignoreCase = true) }
        }
    }
}
