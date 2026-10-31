package com.kangurusiaga.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BabyProfileViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val fentonGrowthApiClient: FentonGrowthApiClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(BabyProfileUiState())
    val uiState: StateFlow<BabyProfileUiState> = _uiState.asStateFlow()

    init {
        loadBabyProfile()
    }

    private fun loadBabyProfile() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby == null) {
                    _uiState.update { it.copy(isLoading = false, baby = null) }
                    return@collectLatest
                }

                val now = System.currentTimeMillis()
                val diffMillis = (now - baby.birthDateEpochMillis).coerceAtLeast(0L)
                val diffDays = (diffMillis / (1000L * 60 * 60 * 24)).toInt()
                val totalWeeks = diffDays / 7
                val months = diffDays / 30
                val remainingWeeks = (diffDays % 30) / 7

                val ageDisplay = if (months > 0) {
                    "${months} Bulan ${remainingWeeks} Minggu"
                } else {
                    "${totalWeeks} Minggu"
                }

                val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                val formattedDate = dateFormatter.format(Date(baby.birthDateEpochMillis))

                val isBblr = baby.birthWeightGram < 2500
                val isPremature = baby.gestationalAgeWeeks < 37

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        baby = baby,
                        formattedBirthDate = formattedDate,
                        chronologicalAgeWeeks = totalWeeks,
                        chronologicalAgeMonths = months,
                        chronologicalAgeDisplay = ageDisplay,
                        isBblr = isBblr,
                        isPremature = isPremature
                    )
                }

                // Observasi data real dari pengukuran pertumbuhan
                observeGrowthMeasurements(baby)
            }
        }
    }

    private fun observeGrowthMeasurements(baby: Baby) {
        val gender = baby.gender ?: Gender.MALE
        val pmaAtBirth = baby.gestationalAgeWeeks.toFloat().coerceIn(22f, 50f)

        viewModelScope.launch {
            combine(
                growthRepository.getLatestMeasurement(baby.id, GrowthParameter.WEIGHT),
                growthRepository.getLatestMeasurement(baby.id, GrowthParameter.LENGTH),
                growthRepository.getLatestMeasurement(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE),
                fentonGrowthRepository.getLatestRecordFlow(baby.id)
            ) { latestWeight, latestLength, latestHead, latestRecord ->
                // Mengambil entri Z-score resmi Fenton terbaru (minggu terakhir)
                val latestZScoreEntry = fentonGrowthApiClient.getLatestZScoreEntry(baby.id)

                // 1. Berat Badan
                val weightEval = if (latestWeight != null) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.WEIGHT,
                        gender = gender,
                        pmaWeeks = latestWeight.pmaWeeks,
                        value = latestWeight.value
                    )
                    val officialPercentile = latestZScoreEntry?.weightPercentile?.let { "P$it" }
                        ?: latestWeight.percentileBadge?.takeIf { it.isNotBlank() }
                        ?: latestRecord?.calculatedWeightPercentile?.takeIf { it.isNotBlank() }
                        ?: eval.percentileBadge

                    val officialStatus = latestRecord?.clinicalClassification?.let {
                        when {
                            it.contains("AGA", ignoreCase = true) -> "Sesuai Usia Gestasi (Ideal)"
                            it.contains("SGA", ignoreCase = true) -> "Kecil untuk Usia Gestasi (SGA)"
                            it.contains("LGA", ignoreCase = true) -> "Besar untuk Usia Gestasi (LGA)"
                            else -> it
                        }
                    } ?: eval.statusText

                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", latestWeight.value),
                        unit = "kg",
                        percentileText = officialPercentile,
                        statusText = officialStatus,
                        isMeasured = true
                    )
                } else if (baby.birthWeightGram > 0) {
                    val weightKg = baby.birthWeightGram / 1000f
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.WEIGHT,
                        gender = gender,
                        pmaWeeks = pmaAtBirth,
                        value = weightKg
                    )
                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", weightKg),
                        unit = "kg",
                        percentileText = eval.percentileBadge,
                        statusText = eval.statusText,
                        isMeasured = true
                    )
                } else {
                    MetricEvaluation(
                        valueFormatted = "-",
                        unit = "kg",
                        percentileText = "-",
                        statusText = "Belum diukur",
                        isMeasured = false
                    )
                }

                // 2. Panjang Badan
                val lengthEval = if (latestLength != null) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.LENGTH,
                        gender = gender,
                        pmaWeeks = latestLength.pmaWeeks,
                        value = latestLength.value
                    )
                    val officialPercentile = latestZScoreEntry?.lengthPercentile?.let { "P$it" }
                        ?: latestLength.percentileBadge?.takeIf { it.isNotBlank() }
                        ?: eval.percentileBadge

                    val officialStatus = latestZScoreEntry?.lengthPercentile?.let { pct ->
                        when {
                            pct in 10..90 -> "Normal (P10–P90)"
                            pct < 10 -> "Pendek (< P10)"
                            else -> "Tinggi (> P90)"
                        }
                    } ?: eval.statusText

                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", latestLength.value),
                        unit = "cm",
                        percentileText = officialPercentile,
                        statusText = officialStatus,
                        isMeasured = true
                    )
                } else if (baby.birthLengthCm > 0f) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.LENGTH,
                        gender = gender,
                        pmaWeeks = pmaAtBirth,
                        value = baby.birthLengthCm
                    )
                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", baby.birthLengthCm),
                        unit = "cm",
                        percentileText = eval.percentileBadge,
                        statusText = eval.statusText,
                        isMeasured = true
                    )
                } else {
                    MetricEvaluation(
                        valueFormatted = "-",
                        unit = "cm",
                        percentileText = "-",
                        statusText = "Belum diukur",
                        isMeasured = false
                    )
                }

                // 3. Lingkar Kepala
                val headEval = if (latestHead != null) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        gender = gender,
                        pmaWeeks = latestHead.pmaWeeks,
                        value = latestHead.value
                    )
                    val officialPercentile = latestZScoreEntry?.headPercentile?.let { "P$it" }
                        ?: latestHead.percentileBadge?.takeIf { it.isNotBlank() }
                        ?: eval.percentileBadge

                    val officialStatus = latestZScoreEntry?.headPercentile?.let { pct ->
                        when {
                            pct in 10..90 -> "Optimal (P10–P90)"
                            pct < 10 -> "Kecil (< P10)"
                            else -> "Besar (> P90)"
                        }
                    } ?: eval.statusText

                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", latestHead.value),
                        unit = "cm",
                        percentileText = officialPercentile,
                        statusText = officialStatus,
                        isMeasured = true
                    )
                } else if (baby.birthHeadCircumferenceCm > 0f) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        gender = gender,
                        pmaWeeks = pmaAtBirth,
                        value = baby.birthHeadCircumferenceCm
                    )
                    MetricEvaluation(
                        valueFormatted = String.format(Locale("id", "ID"), "%.1f", baby.birthHeadCircumferenceCm),
                        unit = "cm",
                        percentileText = eval.percentileBadge,
                        statusText = eval.statusText,
                        isMeasured = true
                    )
                } else {
                    MetricEvaluation(
                        valueFormatted = "-",
                        unit = "cm",
                        percentileText = "-",
                        statusText = "Belum diukur",
                        isMeasured = false
                    )
                }

                // Summary status berbasis klasifikasi klinis Fenton minggu terakhir
                val summaryStatus = when {
                    !weightEval.isMeasured && !lengthEval.isMeasured && !headEval.isMeasured -> "Belum Diukur"
                    latestRecord?.clinicalClassification != null -> {
                        when {
                            latestRecord.clinicalClassification.contains("AGA", ignoreCase = true) -> "Sesuai Kurva Fenton (AGA)"
                            latestRecord.clinicalClassification.contains("SGA", ignoreCase = true) -> "Perlu Pemantauan (SGA < P10)"
                            latestRecord.clinicalClassification.contains("LGA", ignoreCase = true) -> "Besar untuk Usia Gestasi (LGA)"
                            else -> latestRecord.clinicalClassification
                        }
                    }
                    weightEval.statusText.contains("Sesuai", ignoreCase = true) -> "Sesuai Kurva Fenton"
                    weightEval.statusText.contains("Kecil", ignoreCase = true) -> "Perlu Pemantauan (Kecil)"
                    weightEval.statusText.contains("Besar", ignoreCase = true) -> "Besar untuk Masa"
                    else -> "Sesuai Kurva Fenton"
                }

                _uiState.update { state ->
                    state.copy(
                        weightMetric = weightEval,
                        lengthMetric = lengthEval,
                        headMetric = headEval,
                        growthStatusSummary = summaryStatus
                    )
                }
            }.collectLatest { }
        }
    }
}
