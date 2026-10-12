package com.kangurusiaga.app.domain.model

enum class FeedingMethod(val displayName: String) {
    OGT_NGT("OGT/NGT"),
    CUP("Pasi / Dot"),
    SPOON("Menyusu Langsung");

    companion object {
        fun fromString(value: String?): FeedingMethod {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OGT_NGT
        }
    }
}
