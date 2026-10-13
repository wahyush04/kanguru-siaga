package com.kangurusiaga.app.presentation.pmk.reminders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.notification.PmkReminderScheduler
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.usecase.DeletePmkReminderUseCase
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetPmkReminderByIdUseCase
import com.kangurusiaga.app.domain.usecase.SavePmkReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PmkReminderAddEditUiState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val reminderId: Long? = null,
    val timeHour: Int = 8,
    val timeMinute: Int = 0,
    val durationHours: Int = 4,
    val category: String = "Pagi",
    val notes: String = "Estafet Sesi Pagi bersama Ibu",
    val isEnabled: Boolean = true,
    val repeatDaily: Boolean = true,
    val showDeleteConfirmDialog: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val userMessage: String? = null
) {
    val formattedTime: String
        get() = String.format("%02d:%02d", timeHour, timeMinute)
}

@HiltViewModel
class PmkReminderAddEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getPmkReminderByIdUseCase: GetPmkReminderByIdUseCase,
    private val savePmkReminderUseCase: SavePmkReminderUseCase,
    private val deletePmkReminderUseCase: DeletePmkReminderUseCase,
    private val reminderScheduler: PmkReminderScheduler
) : ViewModel() {

    private val navReminderId: Long? = savedStateHandle.get<Long>("reminderId")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(
        PmkReminderAddEditUiState(
            isEditMode = navReminderId != null,
            reminderId = navReminderId
        )
    )
    val uiState: StateFlow<PmkReminderAddEditUiState> = _uiState.asStateFlow()

    init {
        if (navReminderId != null) {
            loadReminder(navReminderId)
        }
    }

    private fun loadReminder(reminderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val reminder = getPmkReminderByIdUseCase(reminderId)
            if (reminder != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        timeHour = reminder.timeHour,
                        timeMinute = reminder.timeMinute,
                        durationHours = reminder.targetHours,
                        category = reminder.category.ifBlank { "Pagi" },
                        notes = reminder.notes,
                        isEnabled = reminder.isEnabled,
                        repeatDaily = reminder.repeatDaily
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(timeHour = hour, timeMinute = minute) }
    }

    fun setDurationHours(hours: Int) {
        _uiState.update { it.copy(durationHours = hours) }
    }

    fun setCategory(category: String) {
        _uiState.update { current ->
            val suggestedNotes = if (current.notes.isBlank() || current.notes.startsWith("Estafet Sesi")) {
                "Estafet Sesi $category"
            } else current.notes
            current.copy(category = category, notes = suggestedNotes)
        }
    }

    fun setNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun setIsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isEnabled = enabled) }
    }

    fun setRepeatDaily(repeat: Boolean) {
        _uiState.update { it.copy(repeatDaily = repeat) }
    }

    fun openDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun closeDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun deleteSchedule() {
        val id = _uiState.value.reminderId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showDeleteConfirmDialog = false) }
            reminderScheduler.cancelReminder(id)
            deletePmkReminderUseCase(id)
            _uiState.update { it.copy(isLoading = false, isDeleted = true) }
        }
    }

    fun saveSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val baby = getBabyProfileUseCase().firstOrNull()
            val babyId = baby?.id ?: 1L
            val state = _uiState.value

            val label = "Estafet Sesi ${state.category}"
            val targetMinutes = state.durationHours * 60

            val reminderToSave = PmkReminder(
                id = state.reminderId ?: 0L,
                babyId = babyId,
                timeHour = state.timeHour,
                timeMinute = state.timeMinute,
                label = label,
                targetMinutes = targetMinutes,
                daysOfWeekMask = 127,
                isEnabled = state.isEnabled,
                category = state.category,
                notes = state.notes,
                repeatDaily = state.repeatDaily
            )

            val savedId = savePmkReminderUseCase(reminderToSave)
            val finalReminder = reminderToSave.copy(id = if (state.reminderId != null) state.reminderId else savedId)

            if (finalReminder.isEnabled) {
                reminderScheduler.scheduleReminder(finalReminder)
            } else {
                reminderScheduler.cancelReminder(finalReminder.id)
            }

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
