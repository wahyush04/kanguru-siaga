package com.kangurusiaga.app.presentation.growth.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.usecase.BatchRowData
import com.kangurusiaga.app.domain.usecase.EnsureInitialGrowthDataUseCase
import com.kangurusiaga.app.domain.usecase.SaveBatchGrowthRecordsUseCase
import com.kangurusiaga.app.domain.util.PmaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GrowthBatchRowState(
    val id: String = UUID.randomUUID().toString(),
    val pmaWeeks: String = "",
    val weightGrams: String = "",
    val headCircumferenceCm: String = "",
    val lengthCm: String = "",
    val pmaError: String? = null
)

data class GrowthBatchUiState(
    val baby: Baby? = null,
    val currentPmaWeeks: Int = 35,
    val rows: List<GrowthBatchRowState> = listOf(
        GrowthBatchRowState(pmaWeeks = "34", weightGrams = "1400", headCircumferenceCm = "26", lengthCm = "40"),
        GrowthBatchRowState(pmaWeeks = "35", weightGrams = "1840", headCircumferenceCm = "27", lengthCm = "41"),
        GrowthBatchRowState(pmaWeeks = "36", weightGrams = "", headCircumferenceCm = "", lengthCm = "")
    ),
    val measurementDateEpochMillis: Long = System.currentTimeMillis(),
    val reminderEnabled: Boolean = true,
    val clinicalNotes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

enum class BatchField {
    PMA, WEIGHT, HEAD_CIRCUMFERENCE, LENGTH
}

@HiltViewModel
class GrowthBatchMeasurementViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val ensureInitialGrowthDataUseCase: EnsureInitialGrowthDataUseCase,
    private val saveBatchGrowthRecordsUseCase: SaveBatchGrowthRecordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthBatchUiState())
    val uiState: StateFlow<GrowthBatchUiState> = _uiState.asStateFlow()

    init {
        loadBabyData()
    }

    private fun loadBabyData() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby != null) {
                    val calc = PmaCalculator.calculate(
                        birthDateEpochMillis = baby.birthDateEpochMillis,
                        measurementDateEpochMillis = _uiState.value.measurementDateEpochMillis,
                        gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                    )
                    _uiState.update { current ->
                        current.copy(
                            baby = baby,
                            currentPmaWeeks = calc.pmaWeeks.toInt().coerceIn(22, 50)
                        )
                    }
                } else {
                    // Fallback to sample data for Bayi Arka Narendra
                    val sample = ensureInitialGrowthDataUseCase.ensureArkaNarendraSampleData()
                    _uiState.update { current ->
                        current.copy(
                            baby = sample,
                            currentPmaWeeks = 35
                        )
                    }
                }
            }
        }
    }

    fun updateField(rowId: String, field: BatchField, value: String) {
        _uiState.update { current ->
            val updatedRows = current.rows.map { row ->
                if (row.id == rowId) {
                    when (field) {
                        BatchField.PMA -> {
                            val clean = value.filter { it.isDigit() }
                            val pmaInt = clean.toIntOrNull()
                            val error = if (pmaInt != null && pmaInt !in 22..50) "22-50" else null
                            row.copy(pmaWeeks = clean, pmaError = error)
                        }
                        BatchField.WEIGHT -> {
                            val clean = value.filter { it.isDigit() || it == '.' || it == ',' }
                            row.copy(weightGrams = clean)
                        }
                        BatchField.HEAD_CIRCUMFERENCE -> {
                            val clean = value.filter { it.isDigit() || it == '.' || it == ',' }
                            row.copy(headCircumferenceCm = clean)
                        }
                        BatchField.LENGTH -> {
                            val clean = value.filter { it.isDigit() || it == '.' || it == ',' }
                            row.copy(lengthCm = clean)
                        }
                    }
                } else {
                    row
                }
            }
            current.copy(rows = updatedRows, errorMessage = null)
        }
    }

    fun addRow() {
        _uiState.update { current ->
            val lastPma = current.rows.lastOrNull()?.pmaWeeks?.toIntOrNull() ?: current.currentPmaWeeks
            val nextPma = (lastPma + 1).coerceAtMost(50)
            val newRow = GrowthBatchRowState(
                pmaWeeks = if (nextPma in 22..50) nextPma.toString() else "",
                weightGrams = "",
                headCircumferenceCm = "",
                lengthCm = ""
            )
            current.copy(rows = current.rows + newRow)
        }
    }

    fun deleteRow(rowId: String) {
        _uiState.update { current ->
            if (current.rows.size > 1) {
                current.copy(rows = current.rows.filterNot { it.id == rowId })
            } else {
                // Keep 1 row but clear its values
                current.copy(
                    rows = listOf(
                        GrowthBatchRowState(pmaWeeks = "", weightGrams = "", headCircumferenceCm = "", lengthCm = "")
                    )
                )
            }
        }
    }

    fun updateDate(epochMillis: Long) {
        _uiState.update { it.copy(measurementDateEpochMillis = epochMillis) }
    }

    fun toggleReminder(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(clinicalNotes = notes) }
    }

    fun saveBatchMeasurements(onSuccess: () -> Unit) {
        val state = _uiState.value
        val baby = state.baby ?: run {
            _uiState.update { it.copy(errorMessage = "Profil bayi belum ditemukan") }
            return
        }

        val parsedRows = mutableListOf<BatchRowData>()

        for ((index, row) in state.rows.withIndex()) {
            val pma = row.pmaWeeks.toIntOrNull()
            if (pma == null || pma !in 22..50) {
                if (row.weightGrams.isNotBlank() || row.headCircumferenceCm.isNotBlank() || row.lengthCm.isNotBlank()) {
                    _uiState.update {
                        it.copy(errorMessage = "GA pada baris ${index + 1} harus antara 22 hingga 50 minggu")
                    }
                    return
                }
                continue // Skip empty row
            }

            val weight = row.weightGrams.replace(',', '.').toDoubleOrNull()
            val head = row.headCircumferenceCm.replace(',', '.').toDoubleOrNull()
            val length = row.lengthCm.replace(',', '.').toDoubleOrNull()

            if (weight == null && head == null && length == null) {
                continue // Empty values row, skip
            }

            parsedRows.add(
                BatchRowData(
                    pmaWeeks = pma,
                    weightGrams = weight,
                    headCircumferenceCm = head,
                    lengthCm = length
                )
            )
        }

        if (parsedRows.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Harap isi minimal satu baris data pengukuran") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = saveBatchGrowthRecordsUseCase(
                babyId = baby.id,
                gender = baby.gender ?: com.kangurusiaga.app.domain.model.Gender.MALE,
                birthDateEpochMillis = baby.birthDateEpochMillis,
                gestationalAgeWeeksAtBirth = baby.gestationalAgeWeeks,
                rows = parsedRows,
                measurementDateEpochMillis = state.measurementDateEpochMillis,
                clinicalNotes = state.clinicalNotes,
                enableReminder = state.reminderEnabled
            )

            result.onSuccess {
                _uiState.update { current ->
                    current.copy(isSaving = false, isSaved = true)
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { current ->
                    current.copy(isSaving = false, errorMessage = err.message ?: "Gagal menyimpan data pengukuran")
                }
            }
        }
    }
}
