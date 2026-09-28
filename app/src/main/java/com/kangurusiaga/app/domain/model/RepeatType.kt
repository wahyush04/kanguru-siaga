package com.kangurusiaga.app.domain.model

enum class RepeatType(val displayName: String) {
    DAILY("Setiap hari"),
    ONCE("Sekali saja");

    companion object {
        fun fromString(value: String?): RepeatType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: DAILY
        }
    }
}
