package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.notification.AlarmScheduler
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Menginisialisasi seluruh jadwal pengingat default (PMK, ASI, dan Skrining Wajib BBLR)
 * segera setelah profil bayi tersedia (terutama saat fresh install selesai onboarding),
 * lalu langsung menyinkronkan dan mendaftarkan alarm terdekat ke AlarmManager.
 */
@Singleton
class InitializeDefaultRemindersUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository,
    private val feedingScheduleRepository: FeedingScheduleRepository,
    private val prematureScreeningRepository: PrematureScreeningRepository,
    private val alarmScheduler: AlarmScheduler
) {
    private val initMutex = Mutex()

    suspend operator fun invoke(baby: Baby) {
        initMutex.withLock {
            try {
                pmkReminderRepository.seedDefaultRemindersIfEmpty(baby.id)
            } catch (e: Exception) {
                AppLogger.e("InitializeDefaultRemindersUseCase", "Gagal seed pengingat PMK: ${e.message}", e)
            }

            try {
                feedingScheduleRepository.seedDefaultSchedulesIfEmpty()
                feedingScheduleRepository.resetAllOffsetToZero()
            } catch (e: Exception) {
                AppLogger.e("InitializeDefaultRemindersUseCase", "Gagal seed jadwal ASI: ${e.message}", e)
            }

            try {
                prematureScreeningRepository.ensureDefaultScreenings(
                    babyId = baby.id,
                    birthDateEpoch = baby.birthDateEpochMillis,
                    gestationalAgeWeeks = baby.gestationalAgeWeeks
                )
            } catch (e: Exception) {
                AppLogger.e("InitializeDefaultRemindersUseCase", "Gagal seed jadwal skrining: ${e.message}", e)
            }

            try {
                alarmScheduler.syncAndScheduleNextAlarm()
            } catch (e: Exception) {
                AppLogger.e("InitializeDefaultRemindersUseCase", "Gagal menjadwalkan alarm berikutnya: ${e.message}", e)
            }
        }
    }
}
