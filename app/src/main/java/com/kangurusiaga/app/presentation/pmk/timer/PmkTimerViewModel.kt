package com.kangurusiaga.app.presentation.pmk.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.PmkTimerManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PmkTimerUiState(
    val timerState: PmkTimerState = PmkTimerState(),
    val baby: Baby? = null,
    val showTargetDialog: Boolean = false,
    val showObservationDialog: Boolean = false,
    val inputTemperature: String = "36.8",
    val inputResponse: String = "Tidur Tenang"
)

sealed interface PmkTimerUiEvent {
    data class SessionCompleted(val session: PmkSession) : PmkTimerUiEvent
    data class Message(val text: String) : PmkTimerUiEvent
}

@HiltViewModel
class PmkTimerViewModel @Inject constructor(
    private val timerManager: PmkTimerManager,
    getBabyProfileUseCase: GetBabyProfileUseCase
) : ViewModel() {

    private val _uiDialogState = MutableStateFlow(
        DialogState(
            showTargetDialog = false,
            showObservationDialog = false,
            inputTemperature = "36.8",
            inputResponse = "Tidur Tenang"
        )
    )

    private val _events = MutableSharedFlow<PmkTimerUiEvent>()
    val events: SharedFlow<PmkTimerUiEvent> = _events.asSharedFlow()

    private data class DialogState(
        val showTargetDialog: Boolean,
        val showObservationDialog: Boolean,
        val inputTemperature: String,
        val inputResponse: String
    )

    val uiState: StateFlow<PmkTimerUiState> = combine(
        timerManager.timerState,
        getBabyProfileUseCase(),
        _uiDialogState
    ) { timer, baby, dialog ->
        PmkTimerUiState(
            timerState = timer,
            baby = baby,
            showTargetDialog = dialog.showTargetDialog,
            showObservationDialog = dialog.showObservationDialog,
            inputTemperature = dialog.inputTemperature,
            inputResponse = dialog.inputResponse
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PmkTimerUiState()
    )

    fun startTimer() {
        val babyId = uiState.value.baby?.id ?: 1L
        val target = uiState.value.timerState.targetDurationMinutes
        timerManager.start(babyId, target)
    }

    fun pauseTimer() {
        timerManager.pause()
    }

    fun resumeTimer() {
        timerManager.resume()
    }

    fun setNotes(notes: String) {
        timerManager.setNotes(notes)
    }

    fun openTargetDialog() {
        _uiDialogState.update { it.copy(showTargetDialog = true) }
    }

    fun closeTargetDialog() {
        _uiDialogState.update { it.copy(showTargetDialog = false) }
    }

    fun updateTargetMinutes(minutes: Int) {
        timerManager.setTargetMinutes(minutes)
        closeTargetDialog()
    }

    fun requestFinishSession() {
        // Open observation dialog to collect temp and response
        _uiDialogState.update { it.copy(showObservationDialog = true) }
    }

    fun closeObservationDialog() {
        _uiDialogState.update { it.copy(showObservationDialog = false) }
    }

    fun updateObservationInput(temp: String, response: String) {
        _uiDialogState.update {
            it.copy(inputTemperature = temp, inputResponse = response)
        }
    }

    fun confirmFinishSession() {
        val tempVal = _uiDialogState.value.inputTemperature.toDoubleOrNull()
        val responseVal = _uiDialogState.value.inputResponse

        timerManager.setBabyObservation(tempVal, responseVal)
        closeObservationDialog()

        viewModelScope.launch {
            val session = timerManager.finishSession()
            if (session != null) {
                _events.emit(PmkTimerUiEvent.SessionCompleted(session))
            }
        }
    }
}
