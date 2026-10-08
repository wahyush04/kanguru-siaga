package com.kangurusiaga.app.presentation.pmk.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.PmkTimerManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetDailyPmkTimelineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Calendar
import javax.inject.Inject

data class PmkTimerUiState(
    val timerState: PmkTimerState = PmkTimerState(),
    val baby: Baby? = null,
    val timeline: DailyTimeline? = null,
    val showCaregiverHandoverSheet: Boolean = false,
    val showPauseSheet: Boolean = false,
    val showFinishConfirmDialog: Boolean = false,
    val showFinishSessionSheet: Boolean = false,
    val isNightModeDim: Boolean = false,
    val handoverCaregiver: PmkCaregiver = PmkCaregiver.AYAH,
    val handoverTemperature: String = "36.8",
    val handoverResponse: String = "Tenang & Rileks",
    val handoverNotes: String = "",
    val pauseReason: PmkPauseReason = PmkPauseReason.NURSING,
    val pauseTemperature: String = "36.8",
    val pauseBehavior: String = "Tenang & Rileks",
    val pauseNotes: String = "",
    val finishTemperature: String = "36.8",
    val finishResponse: String = "Tidur Tenang",
    val finishNotes: String = ""
)

sealed interface PmkTimerUiEvent {
    data class SessionCompleted(val session: PmkSession) : PmkTimerUiEvent
    data class Message(val text: String) : PmkTimerUiEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PmkTimerViewModel @Inject constructor(
    private val timerManager: PmkTimerManager,
    private val getDailyTimelineUseCase: GetDailyPmkTimelineUseCase,
    getBabyProfileUseCase: GetBabyProfileUseCase
) : ViewModel() {

    init {
        timerManager.ensureTickerRunning()
    }

    private data class SheetState(
        val showCaregiverHandoverSheet: Boolean = false,
        val showPauseSheet: Boolean = false,
        val showFinishConfirmDialog: Boolean = false,
        val showFinishSessionSheet: Boolean = false,
        val isNightModeDim: Boolean = false,
        val handoverCaregiver: PmkCaregiver = PmkCaregiver.AYAH,
        val handoverTemperature: String = "36.8",
        val handoverResponse: String = "Tenang & Rileks",
        val handoverNotes: String = "",
        val pauseReason: PmkPauseReason = PmkPauseReason.NURSING,
        val pauseTemperature: String = "36.8",
        val pauseBehavior: String = "Tenang & Rileks",
        val pauseNotes: String = "",
        val finishTemperature: String = "36.8",
        val finishResponse: String = "Tidur Tenang",
        val finishNotes: String = ""
    )

    private val _sheetState = MutableStateFlow(SheetState())
    private val _events = MutableSharedFlow<PmkTimerUiEvent>()
    val events: SharedFlow<PmkTimerUiEvent> = _events.asSharedFlow()

    val uiState: StateFlow<PmkTimerUiState> = getBabyProfileUseCase().flatMapLatest { baby ->
        val babyId = baby?.id ?: 1L
        combine(
            timerManager.timerState,
            getDailyTimelineUseCase(babyId, Calendar.getInstance()),
            _sheetState
        ) { timer, timeline, sheets ->
            PmkTimerUiState(
                timerState = timer,
                baby = baby,
                timeline = timeline,
                showCaregiverHandoverSheet = sheets.showCaregiverHandoverSheet,
                showPauseSheet = sheets.showPauseSheet,
                showFinishConfirmDialog = sheets.showFinishConfirmDialog,
                showFinishSessionSheet = sheets.showFinishSessionSheet,
                isNightModeDim = sheets.isNightModeDim,
                handoverCaregiver = sheets.handoverCaregiver,
                handoverTemperature = sheets.handoverTemperature,
                handoverResponse = sheets.handoverResponse,
                handoverNotes = sheets.handoverNotes,
                pauseReason = sheets.pauseReason,
                pauseTemperature = sheets.pauseTemperature,
                pauseBehavior = sheets.pauseBehavior,
                pauseNotes = sheets.pauseNotes,
                finishTemperature = sheets.finishTemperature,
                finishResponse = sheets.finishResponse,
                finishNotes = sheets.finishNotes
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PmkTimerUiState()
    )

    fun startContinuousTimer(caregiver: PmkCaregiver = PmkCaregiver.IBU) {
        val babyId = uiState.value.baby?.id ?: 1L
        timerManager.start(babyId, caregiver)
    }

    // Caregiver Handover Modal actions
    fun openCaregiverHandoverSheet(defaultCaregiver: PmkCaregiver? = null) {
        val currentCaregiver = uiState.value.timerState.currentCaregiver
        val target = defaultCaregiver ?: when (currentCaregiver) {
            PmkCaregiver.IBU -> PmkCaregiver.AYAH
            PmkCaregiver.AYAH -> PmkCaregiver.PENDAMPING
            PmkCaregiver.PENDAMPING -> PmkCaregiver.IBU
        }
        _sheetState.update {
            it.copy(
                showCaregiverHandoverSheet = true,
                handoverCaregiver = target,
                handoverTemperature = "36.8",
                handoverResponse = "Tenang & Rileks",
                handoverNotes = ""
            )
        }
    }

    fun closeCaregiverHandoverSheet() {
        _sheetState.update { it.copy(showCaregiverHandoverSheet = false) }
    }

    fun selectHandoverCaregiver(caregiver: PmkCaregiver) {
        _sheetState.update { it.copy(handoverCaregiver = caregiver) }
    }

    fun updateHandoverDetails(temperature: String, response: String, notes: String) {
        _sheetState.update {
            it.copy(
                handoverTemperature = temperature,
                handoverResponse = response,
                handoverNotes = notes
            )
        }
    }

    fun confirmCaregiverHandover() {
        val state = _sheetState.value
        val temp = state.handoverTemperature.toDoubleOrNull()
        timerManager.switchCaregiver(
            newCaregiver = state.handoverCaregiver,
            temperature = temp,
            response = state.handoverResponse,
            notes = state.handoverNotes.ifBlank { null }
        )
        closeCaregiverHandoverSheet()
        viewModelScope.launch {
            _events.emit(PmkTimerUiEvent.Message("Estafet pengasuh berhasil dialihkan ke ${state.handoverCaregiver.title}"))
        }
    }

    // Pause Modal actions
    fun openPauseSheet() {
        _sheetState.update {
            it.copy(
                showPauseSheet = true,
                pauseReason = PmkPauseReason.NURSING,
                pauseTemperature = "36.8",
                pauseBehavior = "Tenang & Rileks",
                pauseNotes = ""
            )
        }
    }

    fun closePauseSheet() {
        _sheetState.update { it.copy(showPauseSheet = false) }
    }

    fun selectPauseReason(reason: PmkPauseReason) {
        _sheetState.update { it.copy(pauseReason = reason) }
    }

    fun updatePauseDetails(temperature: String, behavior: String, notes: String) {
        _sheetState.update {
            it.copy(
                pauseTemperature = temperature,
                pauseBehavior = behavior,
                pauseNotes = notes
            )
        }
    }

    fun updatePauseNotes(notes: String) {
        _sheetState.update { it.copy(pauseNotes = notes) }
    }

    fun confirmPause() {
        val state = _sheetState.value
        val temp = state.pauseTemperature.toDoubleOrNull()
        timerManager.pause(
            reason = state.pauseReason,
            note = state.pauseNotes.ifBlank { null },
            temperature = temp,
            response = state.pauseBehavior
        )
        closePauseSheet()
        viewModelScope.launch {
            _events.emit(PmkTimerUiEvent.Message("PMK dijeda untuk ${state.pauseReason.title}"))
        }
    }

    fun resumeTimer() {
        timerManager.resume()
        viewModelScope.launch {
            _events.emit(PmkTimerUiEvent.Message("Kontak kulit dilanjutkan kembali"))
        }
    }

    // Finish session actions
    fun openFinishSessionSheet() {
        _sheetState.update {
            it.copy(
                showFinishSessionSheet = true,
                showFinishConfirmDialog = true,
                finishTemperature = "36.8",
                finishResponse = "Tidur Tenang",
                finishNotes = ""
            )
        }
    }

    fun closeFinishSessionSheet() {
        _sheetState.update {
            it.copy(
                showFinishSessionSheet = false,
                showFinishConfirmDialog = false
            )
        }
    }

    fun updateFinishDetails(temperature: String, response: String, notes: String) {
        _sheetState.update {
            it.copy(
                finishTemperature = temperature,
                finishResponse = response,
                finishNotes = notes
            )
        }
    }

    fun requestFinishSession() {
        openFinishSessionSheet()
    }

    fun closeFinishConfirmDialog() {
        closeFinishSessionSheet()
    }

    fun confirmFinishSession() {
        val state = _sheetState.value
        val temp = state.finishTemperature.toDoubleOrNull()
        closeFinishSessionSheet()
        viewModelScope.launch {
            val session = timerManager.finishSession(
                finalTemp = temp,
                finalResponse = state.finishResponse,
                finalNotes = state.finishNotes.ifBlank { null }
            )
            if (session != null) {
                _events.emit(PmkTimerUiEvent.SessionCompleted(session))
            }
        }
    }

    fun toggleNightModeDim() {
        _sheetState.update { it.copy(isNightModeDim = !it.isNightModeDim) }
    }
}
