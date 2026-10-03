package com.kangurusiaga.app.presentation.growth.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.util.PmaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class AddGrowthUiState(
    val parameter: GrowthParameter = GrowthParameter.WEIGHT,
    val baby: Baby? = null,
    val measurementDateEpochMillis: Long = System.currentTimeMillis(),
    val chronologicalAgeWeeks: Float = 0f,
    val chronologicalAgeDisplay: String = "0 minggu",
    val pmaWeeks: Float = 32f,
    val pmaDisplay: String = "32 minggu",
    val valueText: String = "",
    val noteText: String = "",
    val reminderEnabled: Boolean = true,
    val nextReminderDateEpochMillis: Long = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddGrowthMeasurementViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val parameterName: String = savedStateHandle.get<String>("parameter") ?: GrowthParameter.WEIGHT.name
    val parameter: GrowthParameter = GrowthParameter.fromName(parameterName)

    private val _uiState = MutableStateFlow(
        AddGrowthUiState(parameter = parameter)
    )
    val uiState: StateFlow<AddGrowthUiState> = _uiState.asStateFlow()

    init {
        loadBaby()
    }

    private fun loadBaby() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                _uiState.update { current ->
                    current.copy(baby = baby)
                }
                recalculateAges(baby, _uiState.value.measurementDateEpochMillis)
            }
        }
    }

    fun updateDate(newDateMillis: Long) {
        _uiState.update { current ->
            current.copy(
                measurementDateEpochMillis = newDateMillis,
                nextReminderDateEpochMillis = newDateMillis + TimeUnit.DAYS.toMillis(7)
            )
        }
        recalculateAges(_uiState.value.baby, newDateMillis)
    }

    private fun recalculateAges(baby: Baby?, dateMillis: Long) {
        if (baby == null) return
        val calc = PmaCalculator.calculate(
            birthDateEpochMillis = baby.birthDateEpochMillis,
            measurementDateEpochMillis = dateMillis,
            gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
        )
        _uiState.update {
            it.copy(
                chronologicalAgeWeeks = calc.chronologicalWeeks,
                chronologicalAgeDisplay = "${calc.chronologicalWeeks.toInt()}",
                pmaWeeks = calc.pmaWeeks,
                pmaDisplay = "${calc.pmaWeeks.toInt()}"
            )
        }
    }

    fun updateValue(value: String) {
        _uiState.update { it.copy(valueText = value, errorMessage = null) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(noteText = note) }
    }

    fun toggleReminder(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    fun saveMeasurement(onSuccess: () -> Unit) {
        val state = _uiState.value
        val baby = state.baby ?: run {
            _uiState.update { it.copy(errorMessage = "Profil bayi tidak ditemukan") }
            return
        }

        val rawInput = state.valueText.replace(',', '.').trim()
        val parsedValue = rawInput.toFloatOrNull()
        if (parsedValue == null || parsedValue <= 0f) {
            _uiState.update { it.copy(errorMessage = "Nilai pengukuran tidak valid") }
            return
        }

        // Normalize if user entered grams instead of kg (e.g. 2450 -> 2.45)
        val normalizedValue = if (parameter == GrowthParameter.WEIGHT && parsedValue > 100f) {
            parsedValue / 1000f
        } else {
            parsedValue
        }

        if (normalizedValue !in parameter.minValidValue..parameter.maxValidValue) {
            _uiState.update {
                it.copy(
                    errorMessage = "Nilai harus antara ${parameter.minValidValue} dan ${parameter.maxValidValue} ${parameter.unit}"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val eval = FentonReferenceData.evaluatePercentile(
                parameter = parameter,
                gender = baby.gender,
                pmaWeeks = state.pmaWeeks,
                value = normalizedValue
            )
            val percentileBadge = eval.percentileBadge

            val measurement = GrowthMeasurement(
                babyId = baby.id,
                parameter = parameter,
                measurementDateEpochMillis = state.measurementDateEpochMillis,
                chronologicalAgeWeeks = state.chronologicalAgeWeeks,
                pmaWeeks = state.pmaWeeks,
                value = normalizedValue,
                unit = parameter.unit,
                note = state.noteText.ifBlank { null },
                percentileBadge = percentileBadge
            )

            growthRepository.insertMeasurement(measurement)

            val pmaInt = state.pmaWeeks.toInt().coerceIn(22, 50)
            val pmaDays = ((state.pmaWeeks - pmaInt) * 7).toInt().coerceIn(0, 6)
            val fentonRecord = GrowthRecordEntity(
                babyId = baby.id,
                recordedAt = state.measurementDateEpochMillis,
                pmaWeeks = pmaInt,
                pmaDays = pmaDays,
                weightGrams = if (parameter == GrowthParameter.WEIGHT) normalizedValue.toDouble() * 1000.0 else null,
                lengthCm = if (parameter == GrowthParameter.LENGTH) normalizedValue.toDouble() else null,
                headCircumferenceCm = if (parameter == GrowthParameter.HEAD_CIRCUMFERENCE) normalizedValue.toDouble() else null,
                calculatedWeightPercentile = percentileBadge
            )
            fentonGrowthRepository.saveNewRecord(fentonRecord)

            _uiState.update { it.copy(isSaving = false, isSaved = true) }
            onSuccess()
        }
    }
}
