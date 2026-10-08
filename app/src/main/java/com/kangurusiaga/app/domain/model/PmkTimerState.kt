package com.kangurusiaga.app.domain.model

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class PmkTimerState(
    val status: TimerStatus = TimerStatus.IDLE,
    val activeSessionId: Long = 0L,
    val currentCaregiver: PmkCaregiver = PmkCaregiver.IBU,
    val elapsedSeconds: Long = 0L, // Current continuous session active seconds
    val sessionStartEpoch: Long = 0L,
    val currentSegmentStartEpoch: Long = 0L,
    val pauseElapsedSeconds: Long = 0L,
    val pauseReason: String? = null,
    val todayTotalActiveSeconds: Long = 0L, // Aggregated active seconds for today
    val targetDailyHours: Int = 20, // WHO & IDAI continuous KMC target (20 hours)
    val notes: String = "",
    val babyTemperature: Double? = null,
    val babyResponse: String? = "Tidur Tenang"
) {
    val isRunning: Boolean get() = status == TimerStatus.RUNNING
    val isPaused: Boolean get() = status == TimerStatus.PAUSED
    val isIdle: Boolean get() = status == TimerStatus.IDLE
    val isActive: Boolean get() = status == TimerStatus.RUNNING || status == TimerStatus.PAUSED

    val elapsedMinutes: Int get() = (elapsedSeconds / 60).toInt()

    val todayProgress: Float
        get() {
            val targetSec = targetDailyHours * 3600L
            if (targetSec <= 0) return 0f
            return (todayTotalActiveSeconds.toFloat() / targetSec.toFloat()).coerceIn(0f, 1f)
        }

    val todayPercentage: Int
        get() = (todayProgress * 100).toInt()

    val formattedTime: String
        get() {
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val seconds = elapsedSeconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        }

    val formattedPauseTime: String
        get() {
            val hours = pauseElapsedSeconds / 3600
            val minutes = (pauseElapsedSeconds % 3600) / 60
            val seconds = pauseElapsedSeconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        }

    val formattedTodayTotal: String
        get() {
            val hours = todayTotalActiveSeconds / 3600
            val minutes = (todayTotalActiveSeconds % 3600) / 60
            return "${hours}j ${minutes}m"
        }

    val formattedTodayTotalLong: String
        get() {
            val hours = todayTotalActiveSeconds / 3600
            val minutes = (todayTotalActiveSeconds % 3600) / 60
            return "${hours} Jam ${minutes} Menit"
        }
}
