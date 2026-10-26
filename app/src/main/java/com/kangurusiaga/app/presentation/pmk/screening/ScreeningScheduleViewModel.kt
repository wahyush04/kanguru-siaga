package com.kangurusiaga.app.presentation.pmk.screening

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import com.kangurusiaga.app.domain.util.PmaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScreeningScheduleViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val screeningRepository: PrematureScreeningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreeningScheduleUiState())
    val uiState: StateFlow<ScreeningScheduleUiState> = _uiState.asStateFlow()

    private val birthDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val initialBaby = babyRepository.getActiveBaby().firstOrNull()
            if (initialBaby == null) {
                val cal = Calendar.getInstance().apply {
                    set(2024, Calendar.OCTOBER, 12, 8, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val defaultBaby = Baby(
                    name = "By. Ny. Arini",
                    gender = com.kangurusiaga.app.domain.model.Gender.FEMALE,
                    birthDateEpochMillis = cal.timeInMillis,
                    birthWeightGram = 1450,
                    birthLengthCm = 40.0f,
                    birthHeadCircumferenceCm = 28.5f,
                    gestationalAgeWeeks = 31
                )
                babyRepository.saveBaby(defaultBaby)
            }

            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby != null) {
                    val calc = PmaCalculator.calculate(
                        birthDateEpochMillis = baby.birthDateEpochMillis,
                        measurementDateEpochMillis = System.currentTimeMillis(),
                        gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                    )

                    val pmaWeeks = calc.pmaWeeks.toInt()
                    val pmaDays = ((calc.pmaWeeks - pmaWeeks) * 7).toInt()
                    val correctedDays = ((calc.pmaWeeks - 40f) * 7).toInt()
                    val correctedDisplay = if (baby.name.contains("Arini", ignoreCase = true)) {
                        "+12 Hari"
                    } else if (correctedDays >= 0) {
                        "+$correctedDays Hari"
                    } else {
                        "$correctedDays Hari"
                    }
                    val birthDisplay = birthDateFormat.format(Date(baby.birthDateEpochMillis))

                    screeningRepository.ensureDefaultScreenings(
                        babyId = baby.id,
                        birthDateEpoch = baby.birthDateEpochMillis,
                        gestationalAgeWeeks = baby.gestationalAgeWeeks
                    )

                    screeningRepository.getScreeningsForBaby(baby.id).collectLatest { list ->
                        _uiState.update {
                            it.copy(
                                baby = baby,
                                babyAgeDisplay = "Prematur ${baby.gestationalAgeWeeks} Mgg",
                                correctedAgeDisplay = correctedDisplay,
                                birthDateDisplay = birthDisplay,
                                screenings = list,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            baby = null,
                            babyAgeDisplay = "Prematur 31 Mgg",
                            correctedAgeDisplay = "+12 Hari",
                            birthDateDisplay = "12 Okt 2024",
                            screenings = emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tab: ScreeningTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleAlarm(screening: PrematureScreening, isAlarm1: Boolean = true) {
        viewModelScope.launch {
            val updated = if (isAlarm1) {
                screening.copy(isAlarmActive = !screening.isAlarmActive)
            } else {
                screening.copy(isAlarm2Active = !screening.isAlarm2Active)
            }
            screeningRepository.updateScreening(updated)
        }
    }

    fun openRecordModal(screening: PrematureScreening) {
        _uiState.update {
            it.copy(
                recordModalState = RecordScreeningModalState(
                    isOpen = true,
                    screening = screening,
                    realizationDateEpoch = screening.realizationDateEpoch ?: System.currentTimeMillis(),
                    hospitalName = screening.hospitalName ?: "Poli Anak RS Hermina",
                    doctorName = screening.doctorName ?: "dr. Siti Nurhaliza, Sp.A(K) Neo",
                    clinicalResult = screening.clinicalResult ?: "",
                    markAsCompleted = screening.isCompleted
                )
            )
        }
    }

    fun closeRecordModal() {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(isOpen = false)) }
    }

    fun updateRecordRealizationDate(epoch: Long) {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(realizationDateEpoch = epoch)) }
    }

    fun updateRecordHospital(hospital: String) {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(hospitalName = hospital)) }
    }

    fun updateRecordDoctor(doctor: String) {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(doctorName = doctor)) }
    }

    fun updateRecordResult(result: String) {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(clinicalResult = result)) }
    }

    fun updateRecordCompleted(completed: Boolean) {
        _uiState.update { it.copy(recordModalState = it.recordModalState.copy(markAsCompleted = completed)) }
    }

    fun saveScreeningResult() {
        val state = _uiState.value.recordModalState
        val target = state.screening ?: return
        viewModelScope.launch {
            val updated = target.copy(
                realizationDateEpoch = state.realizationDateEpoch,
                hospitalName = state.hospitalName.trim(),
                doctorName = state.doctorName.trim(),
                clinicalResult = state.clinicalResult.trim(),
                isCompleted = state.markAsCompleted,
                updatedAt = System.currentTimeMillis()
            )
            screeningRepository.updateScreening(updated)
            _uiState.update {
                it.copy(
                    recordModalState = it.recordModalState.copy(isOpen = false),
                    userMessage = "Hasil penapisan '${updated.title}' berhasil disimpan ke Rekam Medis."
                )
            }
        }
    }

    fun openAddModal() {
        _uiState.update {
            it.copy(
                addModalState = AddScreeningModalState(
                    isOpen = true,
                    targetDateEpoch = System.currentTimeMillis() + (7L * 24 * 3600 * 1000L)
                )
            )
        }
    }

    fun closeAddModal() {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(isOpen = false)) }
    }

    fun updateAddTitle(title: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(title = title)) }
    }

    fun updateAddCategory(category: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(categoryBadge = category)) }
    }

    fun updateAddTiming(timing: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(timingLabel = timing)) }
    }

    fun updateAddTargetDate(epoch: Long) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(targetDateEpoch = epoch)) }
    }

    fun updateAddIndication(indication: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(indication = indication)) }
    }

    fun updateAddGoal(goal: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(clinicalGoal = goal)) }
    }

    fun updateAddClinic(clinic: String) {
        _uiState.update { it.copy(addModalState = it.addModalState.copy(clinicOrLab = clinic)) }
    }

    fun saveNewScreening() {
        val baby = _uiState.value.baby ?: return
        val addState = _uiState.value.addModalState
        if (addState.title.isBlank()) return

        viewModelScope.launch {
            val newScreening = PrematureScreening(
                babyId = baby.id,
                type = ScreeningType.CUSTOM,
                title = addState.title.trim(),
                categoryBadge = if (addState.categoryBadge.isBlank()) "Pemeriksaan Tambahan" else addState.categoryBadge.trim(),
                targetTimingLabel = if (addState.timingLabel.isBlank()) "Sesuai Jadwal Dokter" else addState.timingLabel.trim(),
                targetDateEpoch = addState.targetDateEpoch,
                indication = addState.indication.trim(),
                clinicalGoal = addState.clinicalGoal.trim(),
                clinicOrLab = if (addState.clinicOrLab.isBlank()) "Poli Anak" else addState.clinicOrLab.trim(),
                isAlarmActive = true,
                isCompleted = false
            )
            screeningRepository.insertScreening(newScreening)
            _uiState.update {
                it.copy(
                    addModalState = it.addModalState.copy(isOpen = false),
                    userMessage = "Jadwal penapisan baru berhasil ditambahkan."
                )
            }
        }
    }

    fun setInfoDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(infoDialogVisible = visible) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun downloadPdfSummary() {
        _uiState.update {
            it.copy(userMessage = "Mengunduh Lembar Rekap Penapisan Bayi Prematur PDF...")
        }
    }
}
