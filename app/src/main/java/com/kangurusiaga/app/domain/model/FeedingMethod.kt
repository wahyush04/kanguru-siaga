package com.kangurusiaga.app.domain.model

enum class FeedingMethod(val displayName: String) {
    OGT_NGT("OGT/NGT"),
    CUP("Cangkir / Cup"),
    SPOON("Sendok / Pipet");

    companion object {
        fun fromString(value: String?): FeedingMethod {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OGT_NGT
        }
    }
}
