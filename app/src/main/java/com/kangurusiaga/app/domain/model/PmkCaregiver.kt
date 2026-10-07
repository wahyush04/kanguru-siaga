package com.kangurusiaga.app.domain.model

enum class PmkCaregiver(
    val title: String,
    val shortName: String,
    val iconEmoji: String
) {
    IBU("Ibu Siaga", "Ibu", "👩"),
    AYAH("Ayah Aktif", "Ayah", "👨"),
    PENDAMPING("Pendamping Siaga", "Pendamping", "👵");

    companion object {
        fun fromString(value: String?): PmkCaregiver {
            return when (value?.uppercase()) {
                "AYAH" -> AYAH
                "PENDAMPING" -> PENDAMPING
                else -> IBU
            }
        }
    }
}
