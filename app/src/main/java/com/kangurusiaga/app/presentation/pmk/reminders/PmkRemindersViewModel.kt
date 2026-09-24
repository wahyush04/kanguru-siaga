package com.kangurusiaga.app.presentation.pmk.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.notification.PmkReminderScheduler
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.usecase.DeletePmkReminderUseCase
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetPmkRemindersUseCase
import com.kangurusiaga.app.domain.usecase.GetTodayPmkSessionsUseCase
import com.kangurusiaga.app.domain.usecase.SavePmkReminderUseCase
import com.kangurusiaga.app.domain.usecase.TogglePmkReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

data class PmkRemindersUiState(
    val isLoading: Boolean = true,
    val baby: Baby? = null,
    val reminders: List<PmkReminder> = emptyList(),
    val todayCompletedMinutes: Int = 0,
    val todayTargetMinutes: Int = 180, // 3 sessions * 60 min = 180 min
    val todayCompletedSessions: Int = 0,
    val todayTargetSessions: Int = 3,
    val showAddDialog: Boolean = false,
    val showInfoDialog: Boolean = false,
    val message: String? = null
) {
    val progressFraction: Float
        get() = (todayCompletedMinutes.toFloat() / todayTargetMinutes.toFloat()).coerceIn(0f, 1f)

    val progressPercent: Int
        get() = (progressFraction * 100).toInt()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PmkRemindersViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getPmkRemindersUseCase: GetPmkRemindersUseCase,
    private val getTodayPmkSessionsUseCase: GetTodayPmkSessionsUseCase,
    private val togglePmkReminderUseCase: TogglePmkReminderUseCase,
    private val savePmkReminderUseCase: SavePmkReminderUseCase,
    private val deletePmkReminderUseCase: DeletePmkReminderUseCase,
    private val reminderScheduler: PmkReminderScheduler
) : ViewModel() {

    private val _dialogState = MutableStateFlow(
        DialogState(showAdd = false, showInfo = false, message = null)
    )

    private data class DialogState(
        val showAdd: Boolean,
        val showInfo: Boolean,
        val message: String?
    )

    val uiState: StateFlow<PmkRemindersUiState> = getBabyProfileUseCase()
        .flatMapLatest { baby ->
            val babyId = baby?.id ?: 1L
            combine(
                getPmkRemindersUseCase(babyId),
                getTodayPmkSessionsUseCase(babyId),
                _dialogState
            ) { reminders, todaySessions, dialogs ->
                val completedMinutes = todaySessions.sumOf { it.durationMinutes }
                val completedCount = todaySessions.size

                PmkRemindersUiState(
                    isLoading = false,
                    baby = baby,
                    reminders = reminders,
                    todayCompletedMinutes = completedMinutes,
                    todayTargetMinutes = 180,
                    todayCompletedSessions = completedCount,
                    todayTargetSessions = 3,
                    showAddDialog = dialogs.showAdd,
                    showInfoDialog = dialogs.showInfo,
                    message = dialogs.message
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PmkRemindersUiState()
        )

    fun toggleReminder(reminder: PmkReminder) {
        val newEnabled = !reminder.isEnabled
        viewModelScope.launch {
            togglePmkReminderUseCase(reminder.id, newEnabled)
            val updated = reminder.copy(isEnabled = newEnabled)
            reminderScheduler.scheduleReminder(updated)
            val statusText = if (newEnabled) "diaktifkan" else "dinonaktifkan"
            _dialogState.update { it.copy(message = "Pengingat ${reminder.label} $statusText") }
        }
    }

    fun openAddDialog() {
        _dialogState.update { it.copy(showAdd = true) }
    }

    fun closeAddDialog() {
        _dialogState.update { it.copy(showAdd = false) }
    }

    fun openInfoDialog() {
        _dialogState.update { it.copy(showInfo = true) }
    }

    fun closeInfoDialog() {
        _dialogState.update { it.copy(showInfo = false) }
    }

    fun addReminder(hour: Int, minute: Int, label: String, targetMinutes: Int) {
        val babyId = uiState.value.baby?.id ?: 1L
        val newReminder = PmkReminder(
            babyId = babyId,
            timeHour = hour,
            timeMinute = minute,
            label = label,
            targetMinutes = targetMinutes,
            isEnabled = true
        )

        viewModelScope.launch {
            val id = savePmkReminderUseCase(newReminder)
            reminderScheduler.scheduleReminder(newReminder.copy(id = id))
            closeAddDialog()
            _dialogState.update { it.copy(message = "Jadwal pengingat baru berhasil ditambahkan") }
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch {
            reminderScheduler.cancelReminder(reminderId)
            deletePmkReminderUseCase(reminderId)
            _dialogState.update { it.copy(message = "Jadwal pengingat dihapus") }
        }
    }

    fun clearMessage() {
        _dialogState.update { it.copy(message = null) }
    }
}
