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
    val targetDailyHours: Int = 24, // 3 periods x 8 hours = 24 hours
    val currentPeriod: Int = 1, // Period 1 (0-8h), Period 2 (8-16h), Period 3 (16-24h)
    val notes: String = "",
    val babyTemperature: Double? = null,
    val babyResponse: String? = "Tidur Tenang"
) {
    val isRunning: Boolean get() = status == TimerStatus.RUNNING
    val isPaused: Boolean get() = status == TimerStatus.PAUSED
    val isIdle: Boolean get() = status == TimerStatus.IDLE
    val isActive: Boolean get() = status == TimerStatus.RUNNING || status == TimerStatus.PAUSED

    val elapsedMinutes: Int get() = (elapsedSeconds / 60).toInt()

    // 24-Hour Day Progress (0.0 to 1.0)
    val todayProgress: Float
        get() {
            val targetSec = 24 * 3600L
            return (todayTotalActiveSeconds.toFloat() / targetSec.toFloat()).coerceIn(0f, 1f)
        }

    val todayPercentage: Int
        get() = (todayProgress * 100).toInt()

    // Single period target: 8 hours (28,800 seconds)
    val targetPeriodSeconds: Long get() = 8 * 3600L

    // Progress within current period (0 to 28,800s)
    val currentPeriodElapsedSeconds: Long
        get() {
            return when (currentPeriod) {
                1 -> todayTotalActiveSeconds.coerceIn(0L, 8 * 3600L)
                2 -> (todayTotalActiveSeconds - 8 * 3600L).coerceIn(0L, 8 * 3600L)
                3 -> (todayTotalActiveSeconds - 16 * 3600L).coerceIn(0L, 8 * 3600L)
                else -> 8 * 3600L
            }
        }

    val currentPeriodProgress: Float
        get() = (currentPeriodElapsedSeconds.toFloat() / targetPeriodSeconds.toFloat()).coerceIn(0f, 1f)

    val periodLabel: String
        get() = when (currentPeriod) {
            1 -> "Periode 1"
            2 -> "Periode 2"
            3 -> "Periode 3"
            else -> "Selesai 24 Jam"
        }

    val periodRangeLabel: String
        get() = when (currentPeriod) {
            1 -> "0 – 8 Jam"
            2 -> "8 – 16 Jam"
            3 -> "16 – 24 Jam"
            else -> "24 Jam Sempurna"
        }

    val periodFullTitle: String
        get() = "$periodLabel ($periodRangeLabel)"

    val remainingSecondsInPeriod: Long
        get() = (targetPeriodSeconds - currentPeriodElapsedSeconds).coerceAtLeast(0L)

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
