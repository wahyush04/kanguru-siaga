package com.kangurusiaga.app.domain.model

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class PmkTimerState(
    val status: TimerStatus = TimerStatus.IDLE,
    val elapsedSeconds: Long = 0L,
    val targetDurationMinutes: Int = 60,
    val notes: String = "",
    val babyTemperature: Double? = null,
    val babyResponse: String? = "Tidur Tenang"
) {
    val isRunning: Boolean get() = status == TimerStatus.RUNNING
    val isPaused: Boolean get() = status == TimerStatus.PAUSED
    val isIdle: Boolean get() = status == TimerStatus.IDLE
    val isActive: Boolean get() = status == TimerStatus.RUNNING || status == TimerStatus.PAUSED

    val elapsedMinutes: Int get() = (elapsedSeconds / 60).toInt()

    val progress: Float
        get() {
            val targetSec = targetDurationMinutes * 60L
            if (targetSec <= 0) return 0f
            return (elapsedSeconds.toFloat() / targetSec.toFloat()).coerceIn(0f, 1f)
        }

    val formattedTime: String
        get() {
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val seconds = elapsedSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
}
