package com.kangurusiaga.app.presentation.pmk.screening

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import com.kangurusiaga.app.domain.util.PmaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScreeningInputResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val babyRepository: BabyRepository,
    private val screeningRepository: PrematureScreeningRepository
) : ViewModel() {

    private val initialType: ScreeningType = try {
        val typeStr: String? = savedStateHandle["type"]
        if (!typeStr.isNullOrBlank()) ScreeningType.valueOf(typeStr) else ScreeningType.ROP_EYE
    } catch (_: Exception) {
        ScreeningType.ROP_EYE
    }

    private val _uiState = MutableStateFlow(ScreeningInputResultUiState(selectedType = initialType))
    val uiState: StateFlow<ScreeningInputResultUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby != null) {
                    val initials = getInitials(baby.name)
                    val pma = PmaCalculator.calculate(
                        birthDateEpochMillis = baby.birthDateEpochMillis,
                        measurementDateEpochMillis = System.currentTimeMillis(),
                        gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                    )
                    val pmaWeeks = pma.pmaWeeks.toInt().coerceAtLeast(baby.gestationalAgeWeeks)
                    val weightStr = String.format(Locale("id", "ID"), "%,d g", baby.birthWeightGram).replace(',', '.')

                    _uiState.update {
                        it.copy(
                            baby = baby,
                            babyInitials = initials,
                            babyName = baby.name,
                            genderDisplay = if (baby.gender == Gender.FEMALE) "Perempuan" else "Laki-laki",
                            isMale = baby.gender != Gender.FEMALE,
                            pmaWeeksDisplay = "$pmaWeeks Minggu",
                            birthGestationalDisplay = "${baby.gestationalAgeWeeks} mg",
                            birthWeightDisplay = weightStr
                        )
                    }

                    screeningRepository.getScreeningsForBaby(baby.id).collectLatest { list ->
                        _uiState.update { current ->
                            val currentMatch = list.find { it.type == current.selectedType }
                            val notesText = if (current.clinicalNotes.isEmpty()) {
                                currentMatch?.findings ?: currentMatch?.notes ?: ""
                            } else {
                                current.clinicalNotes
                            }
                            val attName = currentMatch?.attachmentName ?: current.attachmentName

                            current.copy(
                                availableScreenings = list,
                                currentScreening = currentMatch,
                                clinicalNotes = notesText,
                                charCount = notesText.length,
                                attachmentName = attName
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectScreeningType(type: ScreeningType) {
        val list = _uiState.value.availableScreenings
        val match = list.find { it.type == type }
        val notesText = match?.findings ?: match?.notes ?: ""
        val attName = match?.attachmentName

        _uiState.update {
            it.copy(
                selectedType = type,
                currentScreening = match,
                clinicalNotes = notesText,
                charCount = notesText.length,
                attachmentName = attName
            )
        }
    }

    fun updateClinicalNotes(notes: String) {
        _uiState.update {
            it.copy(
                clinicalNotes = notes,
                charCount = notes.length,
                isSaved = true
            )
        }
    }

    fun insertQuickPoint(tag: String) {
        val currentNotes = _uiState.value.clinicalNotes
        val insertion = if (currentNotes.isBlank()) "• $tag" else "\n• $tag"
        val updatedNotes = currentNotes + insertion
        _uiState.update {
            it.copy(
                clinicalNotes = updatedNotes,
                charCount = updatedNotes.length,
                isSaved = true
            )
        }
    }

    fun addPhotoAttachment() {
        _uiState.update {
            it.copy(
                attachmentName = "Foto_Pemeriksaan_${it.selectedType.name.lowercase(Locale.ROOT)}.jpg",
                attachmentSize = "2.1 MB • Siap disimpan",
                userMessage = "Foto dokumentasi klinis berhasil diambil."
            )
        }
    }

    fun addPdfAttachment() {
        _uiState.update {
            it.copy(
                attachmentName = "Hasil_Pemeriksaan_${it.selectedType.name.lowercase(Locale.ROOT)}.pdf",
                attachmentSize = "1.8 MB • Siap disimpan",
                userMessage = "Berkas PDF berhasil dilampirkan."
            )
        }
    }

    fun removeAttachment() {
        _uiState.update {
            it.copy(
                attachmentName = null,
                attachmentSize = null,
                userMessage = "Lampiran dihapus."
            )
        }
    }

    fun setHelpDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(helpDialogVisible = visible) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun saveResult(onSuccess: () -> Unit) {
        val target = _uiState.value.currentScreening ?: return
        val notes = _uiState.value.clinicalNotes.trim()
        val attachment = _uiState.value.attachmentName

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updated = target.copy(
                findings = if (notes.isNotEmpty()) notes else target.findings,
                notes = if (notes.isNotEmpty()) notes else target.notes,
                attachmentName = attachment,
                attachmentCount = if (attachment != null) 1 else 0,
                isCompleted = true,
                realizationDateEpoch = target.realizationDateEpoch ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            screeningRepository.updateScreening(updated)
            _uiState.update { it.copy(isSaving = false, userMessage = "Hasil resume klinis berhasil disimpan!") }
            onSuccess()
        }
    }

    private fun getInitials(name: String): String {
        val cleaned = name.replace("By.", "", ignoreCase = true).trim()
        val parts = cleaned.split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.isNotEmpty() -> parts[0].take(2).uppercase(Locale.ROOT)
            else -> "SK"
        }
    }
}
