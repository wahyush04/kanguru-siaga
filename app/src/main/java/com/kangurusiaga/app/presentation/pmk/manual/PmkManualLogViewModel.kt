package com.kangurusiaga.app.presentation.pmk.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.SavePmkSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class PmkManualLogUiState(
    val baby: Baby? = null,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val durationMinutes: Int = 60,
    val babyTemperature: String = "36.8",
    val babyResponse: String = "Tidur Tenang",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            return sdf.format(dateEpochMillis)
        }

    val formattedStartTime: String
        get() = String.format("%02d:%02d", startHour, startMinute)
}

sealed interface PmkManualLogEvent {
    data object SavedSuccess : PmkManualLogEvent
    data class Error(val message: String) : PmkManualLogEvent
}

@HiltViewModel
class PmkManualLogViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val savePmkSessionUseCase: SavePmkSessionUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(
        PmkManualLogUiState(
            startHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            startMinute = Calendar.getInstance().get(Calendar.MINUTE)
        )
    )

    private val _events = MutableSharedFlow<PmkManualLogEvent>()
    val events: SharedFlow<PmkManualLogEvent> = _events.asSharedFlow()

    val uiState: StateFlow<PmkManualLogUiState> = combine(
        getBabyProfileUseCase(),
        _formState
    ) { baby, form ->
        form.copy(baby = baby)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _formState.value
    )

    fun onDateChanged(epochMillis: Long) {
        _formState.update { it.copy(dateEpochMillis = epochMillis) }
    }

    fun onStartTimeChanged(hour: Int, minute: Int) {
        _formState.update { it.copy(startHour = hour, startMinute = minute) }
    }

    fun onDurationChanged(minutes: Int) {
        _formState.update { it.copy(durationMinutes = minutes) }
    }

    fun onTemperatureChanged(temp: String) {
        _formState.update { it.copy(babyTemperature = temp) }
    }

    fun onResponseChanged(response: String) {
        _formState.update { it.copy(babyResponse = response) }
    }

    fun onNotesChanged(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun saveManualSession() {
        val current = _formState.value
        val babyId = uiState.value.baby?.id ?: 1L

        val cal = Calendar.getInstance().apply {
            timeInMillis = current.dateEpochMillis
            set(Calendar.HOUR_OF_DAY, current.startHour)
            set(Calendar.MINUTE, current.startMinute)
            set(Calendar.SECOND, 0)
        }
        val startTime = cal.timeInMillis
        val endTime = startTime + (current.durationMinutes * 60 * 1000L)

        val tempVal = current.babyTemperature.toDoubleOrNull()

        val session = PmkSession(
            babyId = babyId,
            startTimeEpoch = startTime,
            endTimeEpoch = endTime,
            durationMinutes = current.durationMinutes,
            targetDurationMinutes = 60,
            source = PmkSource.MANUAL,
            babyTemperature = tempVal,
            babyResponse = current.babyResponse,
            notes = current.notes.ifBlank { null }
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            try {
                savePmkSessionUseCase(session)
                _events.emit(PmkManualLogEvent.SavedSuccess)
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, errorMessage = e.localizedMessage) }
                _events.emit(PmkManualLogEvent.Error(e.localizedMessage ?: "Gagal menyimpan"))
            }
        }
    }
}
