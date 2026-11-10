package com.kangurusiaga.app.presentation.pmk.screening

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.ScreeningAttachmentManager
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CustomScreeningInputResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val babyRepository: BabyRepository,
    private val screeningRepository: PrematureScreeningRepository,
    private val attachmentManager: ScreeningAttachmentManager
) : ViewModel() {

    private val screeningIdArg: Long = savedStateHandle["screeningId"] ?: -1L

    private val _uiState = MutableStateFlow(
        CustomScreeningInputResultUiState(
            screeningId = screeningIdArg
        )
    )
    val uiState: StateFlow<CustomScreeningInputResultUiState> = _uiState.asStateFlow()

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

                    if (screeningIdArg > 0) {
                        screeningRepository.getScreeningsForBaby(baby.id).collectLatest { list ->
                            val match = list.find { it.id == screeningIdArg }
                            if (match != null) {
                                val notesText = if (_uiState.value.clinicalNotes.isEmpty()) {
                                    match.findings ?: match.notes ?: ""
                                } else {
                                    _uiState.value.clinicalNotes
                                }
                                val attName = match.attachmentName ?: _uiState.value.attachmentName
                                val attUri = match.attachmentUri ?: _uiState.value.attachmentUri
                                val attSize = if (attUri != null) {
                                    attachmentManager.getFormattedFileSize(attUri) ?: _uiState.value.attachmentSize
                                } else {
                                    _uiState.value.attachmentSize
                                }
                                val isImg = attName?.lowercase(Locale.ROOT)?.endsWith(".pdf") != true

                                _uiState.update { current ->
                                    current.copy(
                                        currentScreening = match,
                                        customTitle = if (current.customTitle.isBlank()) match.title else current.customTitle,
                                        clinicalNotes = notesText,
                                        charCount = notesText.length,
                                        attachmentName = attName,
                                        attachmentUri = attUri,
                                        attachmentSize = attSize,
                                        isAttachmentImage = isImg
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateCustomTitle(title: String) {
        _uiState.update {
            it.copy(customTitle = title)
        }
    }

    fun updateClinicalNotes(notes: String) {
        _uiState.update {
            it.copy(
                clinicalNotes = notes,
                charCount = notes.length,
                isSaved = false
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

    fun createTempCameraUri(): Uri {
        return attachmentManager.createTempCameraUri()
    }

    fun onPhotoCaptured(tempUri: Uri) {
        val babyId = _uiState.value.baby?.id ?: return
        viewModelScope.launch {
            try {
                val result = attachmentManager.saveCapturedPhoto(tempUri, babyId, ScreeningType.CUSTOM)
                val prevUri = _uiState.value.attachmentUri
                if (prevUri != null && prevUri != result.fileUri) {
                    attachmentManager.deleteAttachment(prevUri)
                }
                _uiState.update {
                    it.copy(
                        attachmentName = result.fileName,
                        attachmentSize = "${result.formattedSize} • Siap disimpan",
                        attachmentUri = result.fileUri,
                        isAttachmentImage = result.isImage,
                        userMessage = "Foto dokumentasi klinis berhasil diambil."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(userMessage = e.message ?: "Gagal memproses foto kamera.")
                }
            }
        }
    }

    fun onDocumentSelected(sourceUri: Uri) {
        val babyId = _uiState.value.baby?.id ?: return
        viewModelScope.launch {
            try {
                val result = attachmentManager.saveDocument(sourceUri, babyId, ScreeningType.CUSTOM)
                val prevUri = _uiState.value.attachmentUri
                if (prevUri != null && prevUri != result.fileUri) {
                    attachmentManager.deleteAttachment(prevUri)
                }
                _uiState.update {
                    it.copy(
                        attachmentName = result.fileName,
                        attachmentSize = "${result.formattedSize} • Siap disimpan",
                        attachmentUri = result.fileUri,
                        isAttachmentImage = result.isImage,
                        userMessage = "Berkas dokumen berhasil dilampirkan."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(userMessage = e.message ?: "Gagal melampirkan berkas dokumen.")
                }
            }
        }
    }

    fun removeAttachment() {
        val currentUri = _uiState.value.attachmentUri
        viewModelScope.launch {
            if (currentUri != null) {
                attachmentManager.deleteAttachment(currentUri)
            }
            _uiState.update {
                it.copy(
                    attachmentName = null,
                    attachmentSize = null,
                    attachmentUri = null,
                    isAttachmentImage = false,
                    userMessage = "Lampiran dihapus."
                )
            }
        }
    }

    fun openCurrentAttachment(context: Context) {
        val uri = _uiState.value.attachmentUri
        if (uri.isNullOrBlank()) {
            _uiState.update { it.copy(userMessage = "Tidak ada lampiran untuk dibuka.") }
            return
        }
        val intent = attachmentManager.getOpenAttachmentIntent(uri)
        if (intent != null) {
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                _uiState.update { it.copy(userMessage = "Tidak ditemukan aplikasi untuk membuka berkas ini.") }
            }
        } else {
            _uiState.update { it.copy(userMessage = "Berkas lampiran tidak ditemukan di penyimpanan.") }
        }
    }

    fun setHelpDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(helpDialogVisible = visible) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun saveResult(onSuccess: () -> Unit) {
        val state = _uiState.value
        val baby = state.baby ?: return
        val finalTitle = state.customTitle.trim().ifBlank { "Skrining Tambahan" }
        val notes = state.clinicalNotes.trim()
        val attachment = state.attachmentName
        val attUri = state.attachmentUri

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            if (state.currentScreening == null) {
                val newScreening = PrematureScreening(
                    babyId = baby.id,
                    type = ScreeningType.CUSTOM,
                    title = finalTitle,
                    categoryBadge = "Skrining Tambahan Kustom",
                    targetTimingLabel = "Pemeriksaan Spesialis Tambahan",
                    targetDateEpoch = System.currentTimeMillis(),
                    realizationDateEpoch = System.currentTimeMillis(),
                    indication = if (notes.isNotBlank()) notes else "Pemeriksaan spesialis penunjang",
                    clinicalGoal = "Pemeriksaan spesialis penunjang evaluasi tumbuh kembang bayi prematur.",
                    clinicOrLab = "Poli Spesialis / Rujukan",
                    isAlarmActive = false,
                    isCompleted = true,
                    findings = if (notes.isNotEmpty()) notes else "Hasil pemeriksaan tercatat dalam resume klinis.",
                    notes = notes,
                    attachmentName = attachment,
                    attachmentUri = attUri,
                    attachmentCount = if (attachment != null) 1 else 0,
                    updatedAt = System.currentTimeMillis()
                )
                screeningRepository.insertScreening(newScreening)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        userMessage = "Hasil resume klinis '$finalTitle' berhasil disimpan!"
                    )
                }
                onSuccess()
            } else {
                val target = state.currentScreening
                val updated = target.copy(
                    title = finalTitle,
                    findings = if (notes.isNotEmpty()) notes else target.findings,
                    notes = if (notes.isNotEmpty()) notes else target.notes,
                    attachmentName = attachment,
                    attachmentUri = attUri,
                    attachmentCount = if (attachment != null) 1 else 0,
                    isCompleted = true,
                    realizationDateEpoch = target.realizationDateEpoch ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                screeningRepository.updateScreening(updated)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        userMessage = "Hasil resume klinis '$finalTitle' berhasil disimpan!"
                    )
                }
                onSuccess()
            }
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

