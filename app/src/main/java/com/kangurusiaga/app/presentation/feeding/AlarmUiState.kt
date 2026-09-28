package com.kangurusiaga.app.presentation.feeding

import com.kangurusiaga.app.domain.model.FeedingSchedule

data class AlarmUiState(
    val schedules: List<FeedingSchedule> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val selectedScheduleForEdit: FeedingSchedule? = null,
    val isAddSheetVisible: Boolean = false,
    val isEditSheetVisible: Boolean = false,
    val isInfoDialogVisible: Boolean = false,
    val schedulePendingDelete: FeedingSchedule? = null
)
