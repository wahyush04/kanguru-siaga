package com.kangurusiaga.app.presentation.growth.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.notification.AlarmScheduler
import com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.usecase.BatchRowData
import com.kangurusiaga.app.domain.usecase.EnsureInitialGrowthDataUseCase
import com.kangurusiaga.app.domain.usecase.SaveBatchGrowthRecordsUseCase
import com.kangurusiaga.app.domain.util.PmaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    val currentPmaWeeks: Int = 0,
    val rows: List<GrowthBatchRowState> = emptyList(),
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
    private val saveBatchGrowthRecordsUseCase: SaveBatchGrowthRecordsUseCase,
    private val fentonGrowthApiClient: FentonGrowthApiClient,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthBatchUiState())
    val uiState: StateFlow<GrowthBatchUiState> = _uiState.asStateFlow()

    init {
        loadBabyData()
    }

    private fun loadBabyData() {
        viewModelScope.launch {
            ensureInitialGrowthDataUseCase.purgeMockDataIfPresent()

            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby == null) {
                    _uiState.update { it.copy(baby = null, rows = emptyList()) }
                    return@collectLatest
                }

                val calc = PmaCalculator.calculate(
                    birthDateEpochMillis = baby.birthDateEpochMillis,
                    measurementDateEpochMillis = _uiState.value.measurementDateEpochMillis,
                    gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                )
                val currentPma = calc.pmaWeeks.toInt().coerceIn(22, 50)

                // Muat semua riwayat pengukuran yang ada di Room DB
                val weights = try {
                    growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.WEIGHT).first()
                } catch (_: Exception) {
                    emptyList()
                }
                val lengths = try {
                    growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.LENGTH).first()
                } catch (_: Exception) {
                    emptyList()
                }
                val headCircs = try {
                    growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE).first()
                } catch (_: Exception) {
                    emptyList()
                }
                val fentonRecords = try {
                    fentonGrowthRepository.getPatientRecords(baby.id).first()
                } catch (_: Exception) {
                    emptyList()
                }

                val rowMap = linkedMapOf<Int, GrowthBatchRowState>()

                // 1. Masukkan titik lahir bayi jika ada
                val birthPma = baby.gestationalAgeWeeks.coerceIn(22, 50)
                if (baby.birthWeightGram > 0 || baby.birthLengthCm > 0f || baby.birthHeadCircumferenceCm > 0f) {
                    rowMap[birthPma] = GrowthBatchRowState(
                        pmaWeeks = birthPma.toString(),
                        weightGrams = if (baby.birthWeightGram > 0) baby.birthWeightGram.toString() else "",
                        lengthCm = if (baby.birthLengthCm > 0f) formatNumber(baby.birthLengthCm) else "",
                        headCircumferenceCm = if (baby.birthHeadCircumferenceCm > 0f) formatNumber(baby.birthHeadCircumferenceCm) else ""
                    )
                }

                // 2. Gabungkan data riwayat Fenton (baby_growth_records)
                for (record in fentonRecords) {
                    val pma = record.pmaWeeks.coerceIn(22, 50)
                    // Jangan prefill baris kedua jika record di PMA berbeda ini adalah duplikasi data lahir
                    val isDuplicateOfBirth = pma != birthPma &&
                            (record.weightGrams == null || record.weightGrams.toInt() == baby.birthWeightGram) &&
                            (record.lengthCm == null || record.lengthCm.toFloat() == baby.birthLengthCm) &&
                            (record.headCircumferenceCm == null || record.headCircumferenceCm.toFloat() == baby.birthHeadCircumferenceCm)
                    if (isDuplicateOfBirth) continue

                    val row = rowMap.getOrPut(pma) { GrowthBatchRowState(pmaWeeks = pma.toString()) }
                    var updated = row
                    if (record.weightGrams != null && record.weightGrams > 0) {
                        updated = updated.copy(weightGrams = record.weightGrams.toInt().toString())
                    }
                    if (record.lengthCm != null && record.lengthCm > 0) {
                        updated = updated.copy(lengthCm = formatNumber(record.lengthCm.toFloat()))
                    }
                    if (record.headCircumferenceCm != null && record.headCircumferenceCm > 0) {
                        updated = updated.copy(headCircumferenceCm = formatNumber(record.headCircumferenceCm.toFloat()))
                    }
                    rowMap[pma] = updated
                }

                // 3. Gabungkan data berat (growth_measurements)
                for (w in weights) {
                    val pma = w.pmaWeeks.toInt().coerceIn(22, 50)
                    val grams = if (w.value < 100f) (w.value * 1000f).toInt() else w.value.toInt()
                    if (pma != birthPma && w.note == "Data Saat Ini" && grams == baby.birthWeightGram) continue
                    val row = rowMap.getOrPut(pma) { GrowthBatchRowState(pmaWeeks = pma.toString()) }
                    if (grams > 0) {
                        rowMap[pma] = row.copy(weightGrams = grams.toString())
                    }
                }

                // 4. Gabungkan data panjang (growth_measurements)
                for (l in lengths) {
                    val pma = l.pmaWeeks.toInt().coerceIn(22, 50)
                    if (pma != birthPma && l.note == "Data Saat Ini" && l.value == baby.birthLengthCm) continue
                    val row = rowMap.getOrPut(pma) { GrowthBatchRowState(pmaWeeks = pma.toString()) }
                    if (l.value > 0f) {
                        rowMap[pma] = row.copy(lengthCm = formatNumber(l.value))
                    }
                }

                // 5. Gabungkan data lingkar kepala (growth_measurements)
                for (hc in headCircs) {
                    val pma = hc.pmaWeeks.toInt().coerceIn(22, 50)
                    if (pma != birthPma && hc.note == "Data Saat Ini" && hc.value == baby.birthHeadCircumferenceCm) continue
                    val row = rowMap.getOrPut(pma) { GrowthBatchRowState(pmaWeeks = pma.toString()) }
                    if (hc.value > 0f) {
                        rowMap[pma] = row.copy(headCircumferenceCm = formatNumber(hc.value))
                    }
                }

                val existingSorted = rowMap.values.sortedBy { it.pmaWeeks.toIntOrNull() ?: 0 }

                // 6. Buat baris baru di bawahnya untuk input data baru (tanpa prefill BB, LK, PB)
                val lastPma = existingSorted.lastOrNull()?.pmaWeeks?.toIntOrNull() ?: currentPma
                val targetNextPma = if (existingSorted.size == 1 && currentPma > birthPma) {
                    currentPma
                } else {
                    (lastPma + 1).coerceAtMost(50)
                }

                val newEmptyRow = GrowthBatchRowState(
                    pmaWeeks = if (targetNextPma in 22..50) targetNextPma.toString() else "",
                    weightGrams = "",
                    headCircumferenceCm = "",
                    lengthCm = ""
                )

                val combinedRows = if (existingSorted.isEmpty()) {
                    listOf(
                        GrowthBatchRowState(
                            pmaWeeks = birthPma.toString(),
                            weightGrams = "",
                            headCircumferenceCm = "",
                            lengthCm = ""
                        )
                    )
                } else {
                    existingSorted + newEmptyRow
                }

                _uiState.update { current ->
                    current.copy(
                        baby = baby,
                        currentPmaWeeks = currentPma,
                        rows = combinedRows
                    )
                }
            }
        }
    }

    private fun formatNumber(value: Float): String {
        return if (value % 1.0f == 0f) {
            value.toInt().toString()
        } else {
            java.text.DecimalFormat("#.#", java.text.DecimalFormatSymbols(java.util.Locale.US)).format(value)
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
                // Fetch API Fenton segera dengan data terbaru dan tunggu hingga selesai
                try {
                    val weights = growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.WEIGHT).first()
                    val lengths = growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.LENGTH).first()
                    val headCircs = growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE).first()
                    val apiResult = fentonGrowthApiClient.fetchPlotChart(baby, weights + lengths + headCircs)
                    if (apiResult.isFailure) {
                        AppLogger.w("GrowthBatchMeasurementViewModel", "Gagal refresh gambar Fenton via API: ${apiResult.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    AppLogger.w("GrowthBatchMeasurementViewModel", "Exception saat auto-refresh Fenton: ${e.message}")
                }
                if (state.reminderEnabled) {
                    try {
                        alarmScheduler.scheduleFentonWeeklyReminder(baby.id, true)
                    } catch (e: Exception) {
                        AppLogger.w("GrowthBatchMeasurementViewModel", "Gagal menjadwalkan reminder mingguan Fenton: ${e.message}")
                    }
                }
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
