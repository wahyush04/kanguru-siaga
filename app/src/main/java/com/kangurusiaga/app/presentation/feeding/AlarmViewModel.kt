package com.kangurusiaga.app.presentation.feeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import com.kangurusiaga.app.domain.usecase.feeding.AddFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.DeleteFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ObserveFeedingSchedulesUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ToggleFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.UpdateFeedingScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val observeFeedingSchedulesUseCase: ObserveFeedingSchedulesUseCase,
    private val addFeedingScheduleUseCase: AddFeedingScheduleUseCase,
    private val updateFeedingScheduleUseCase: UpdateFeedingScheduleUseCase,
    private val deleteFeedingScheduleUseCase: DeleteFeedingScheduleUseCase,
    private val toggleFeedingScheduleUseCase: ToggleFeedingScheduleUseCase,
    private val repository: FeedingScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmUiState(isLoading = true))
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed 12 default slots on first launch if database is empty
            repository.seedDefaultSchedulesIfEmpty()

            // Observe schedules from local database
            observeFeedingSchedulesUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = "Gagal memuat jadwal: ${error.localizedMessage}"
                        )
                    }
                }
                .collect { schedules ->
                    _uiState.update {
                        it.copy(
                            schedules = schedules,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onToggleSchedule(scheduleId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            val result = toggleFeedingScheduleUseCase(scheduleId, isEnabled)
            result.onFailure { error ->
                _uiState.update {
                    it.copy(userMessage = error.localizedMessage ?: "Gagal mengubah status jadwal")
                }
            }
        }
    }

    fun onOpenAddSchedule() {
        _uiState.update { it.copy(isAddSheetVisible = true) }
    }

    fun onCloseAddSchedule() {
        _uiState.update { it.copy(isAddSheetVisible = false) }
    }

    fun onOpenEditSchedule(schedule: FeedingSchedule) {
        _uiState.update {
            it.copy(
                selectedScheduleForEdit = schedule,
                isEditSheetVisible = true
            )
        }
    }

    fun onCloseEditSchedule() {
        _uiState.update {
            it.copy(
                selectedScheduleForEdit = null,
                isEditSheetVisible = false
            )
        }
    }

    fun onSaveNewSchedule(
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: RepeatType
    ) {
        viewModelScope.launch {
            val newSchedule = FeedingSchedule(
                hour = hour,
                minute = minute,
                volumeMl = volumeMl,
                method = method,
                note = note?.trim()?.ifEmpty { null },
                isEnabled = true,
                reminderEnabled = reminderEnabled,
                reminderOffsetMinutes = 10,
                repeatType = repeatType
            )

            val result = addFeedingScheduleUseCase(newSchedule)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isAddSheetVisible = false,
                        userMessage = "Jadwal berhasil ditambahkan"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(userMessage = error.localizedMessage ?: "Gagal menambahkan jadwal")
                }
            }
        }
    }

    fun onUpdateSchedule(
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: RepeatType
    ) {
        viewModelScope.launch {
            val existing = _uiState.value.selectedScheduleForEdit
            val updated = existing?.copy(
                hour = hour,
                minute = minute,
                volumeMl = volumeMl,
                method = method,
                note = note?.trim()?.ifEmpty { null },
                reminderEnabled = reminderEnabled,
                repeatType = repeatType,
                updatedAt = System.currentTimeMillis()
            ) ?: FeedingSchedule(
                id = scheduleId,
                hour = hour,
                minute = minute,
                volumeMl = volumeMl,
                method = method,
                note = note?.trim()?.ifEmpty { null },
                isEnabled = true,
                reminderEnabled = reminderEnabled,
                reminderOffsetMinutes = 10,
                repeatType = repeatType
            )

            val result = updateFeedingScheduleUseCase(updated)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isEditSheetVisible = false,
                        selectedScheduleForEdit = null,
                        userMessage = "Jadwal berhasil diperbarui"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(userMessage = error.localizedMessage ?: "Gagal memperbarui jadwal")
                }
            }
        }
    }

    fun onRequestDeleteSchedule(schedule: FeedingSchedule) {
        _uiState.update { it.copy(schedulePendingDelete = schedule) }
    }

    fun onCancelDeleteSchedule() {
        _uiState.update { it.copy(schedulePendingDelete = null) }
    }

    fun onConfirmDeleteSchedule() {
        val schedule = _uiState.value.schedulePendingDelete ?: return
        viewModelScope.launch {
            val result = deleteFeedingScheduleUseCase(schedule.id)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        schedulePendingDelete = null,
                        isEditSheetVisible = false,
                        selectedScheduleForEdit = null,
                        userMessage = "Jadwal berhasil dihapus"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        schedulePendingDelete = null,
                        userMessage = error.localizedMessage ?: "Gagal menghapus jadwal"
                    )
                }
            }
        }
    }

    fun onOpenInfoDialog() {
        _uiState.update { it.copy(isInfoDialogVisible = true) }
    }

    fun onDismissInfoDialog() {
        _uiState.update { it.copy(isInfoDialogVisible = false) }
    }

    fun onClearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
