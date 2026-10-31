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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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

                // Ambil nilai riil minggu terakhir dari data pertumbuhan bayi (Z-Score Fenton / Riwayat Rekor / Pengukuran)
                val weightKg: Float? = latestZScoreEntry?.weightGrams?.let { (it / 1000.0).toFloat() }
                    ?: latestRecord?.weightGrams?.let { (it / 1000.0).toFloat() }
                    ?: latestWeight?.value
                    ?: baby.birthWeightGram.takeIf { it > 0 }?.let { it / 1000f }

                val lengthCm: Float? = latestZScoreEntry?.lengthCm?.toFloat()
                    ?: latestRecord?.lengthCm?.toFloat()
                    ?: latestLength?.value
                    ?: baby.birthLengthCm.takeIf { it > 0f }

                val headCm: Float? = latestZScoreEntry?.headCm?.toFloat()
                    ?: latestRecord?.headCircumferenceCm?.toFloat()
                    ?: latestHead?.value
                    ?: baby.birthHeadCircumferenceCm.takeIf { it > 0f }

                val pmaWeeksForEval: Float = latestZScoreEntry?.gaWeeks
                    ?: latestRecord?.pmaWeeks?.toFloat()
                    ?: latestWeight?.pmaWeeks
                    ?: pmaAtBirth

                // Format angka desimal sesuai standar Indonesia (koma sebagai pemisah desimal)
                val weightDf = DecimalFormat("#.##", DecimalFormatSymbols(Locale("id", "ID"))).apply {
                    minimumFractionDigits = 1
                    maximumFractionDigits = 2
                }

                // 1. Berat Badan
                val evalW = if (weightKg != null) {
                    FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.WEIGHT,
                        gender = gender,
                        pmaWeeks = pmaWeeksForEval,
                        value = weightKg
                    )
                } else null

                val officialWeightPercentile = latestZScoreEntry?.weightPercentile?.let { "P$it" }
                    ?: latestRecord?.calculatedWeightPercentile?.takeIf { it.isNotBlank() }
                    ?: latestWeight?.percentileBadge?.takeIf { it.isNotBlank() }
                    ?: evalW?.percentileBadge
                    ?: "-"

                val officialWeightStatus = latestRecord?.clinicalClassification?.let {
                    when {
                        it.contains("AGA", ignoreCase = true) -> "Sesuai Usia Gestasi (Ideal)"
                        it.contains("SGA", ignoreCase = true) -> "Kecil untuk Usia Gestasi (SGA)"
                        it.contains("LGA", ignoreCase = true) -> "Besar untuk Usia Gestasi (LGA)"
                        else -> it
                    }
                } ?: latestZScoreEntry?.weightPercentile?.let { pct ->
                    when {
                        pct in 10..90 -> "Sesuai Usia Gestasi"
                        pct < 10 -> "Kecil untuk Usia Gestasi (SGA)"
                        else -> "Besar untuk Usia Gestasi (LGA)"
                    }
                } ?: evalW?.statusText ?: "Belum diukur"

                val weightEval = MetricEvaluation(
                    valueFormatted = if (weightKg != null) weightDf.format(weightKg) else "-",
                    unit = "kg",
                    percentileText = officialWeightPercentile,
                    statusText = officialWeightStatus,
                    isMeasured = weightKg != null
                )

                // 2. Panjang Badan
                val evalL = if (lengthCm != null) {
                    FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.LENGTH,
                        gender = gender,
                        pmaWeeks = pmaWeeksForEval,
                        value = lengthCm
                    )
                } else null

                val officialLengthPercentile = latestZScoreEntry?.lengthPercentile?.let { "P$it" }
                    ?: latestLength?.percentileBadge?.takeIf { it.isNotBlank() }
                    ?: evalL?.percentileBadge
                    ?: "-"

                val officialLengthStatus = latestZScoreEntry?.lengthPercentile?.let { pct ->
                    when {
                        pct in 10..90 -> "Normal (P10–P90)"
                        pct < 10 -> "Pendek (< P10)"
                        else -> "Tinggi (> P90)"
                    }
                } ?: evalL?.statusText ?: "Belum diukur"

                val lengthEval = MetricEvaluation(
                    valueFormatted = if (lengthCm != null) String.format(Locale("id", "ID"), "%.1f", lengthCm) else "-",
                    unit = "cm",
                    percentileText = officialLengthPercentile,
                    statusText = officialLengthStatus,
                    isMeasured = lengthCm != null
                )

                // 3. Lingkar Kepala
                val evalH = if (headCm != null) {
                    FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        gender = gender,
                        pmaWeeks = pmaWeeksForEval,
                        value = headCm
                    )
                } else null

                val officialHeadPercentile = latestZScoreEntry?.headPercentile?.let { "P$it" }
                    ?: latestHead?.percentileBadge?.takeIf { it.isNotBlank() }
                    ?: evalH?.percentileBadge
                    ?: "-"

                val officialHeadStatus = latestZScoreEntry?.headPercentile?.let { pct ->
                    when {
                        pct in 10..90 -> "Optimal (P10–P90)"
                        pct < 10 -> "Kecil (< P10)"
                        else -> "Besar (> P90)"
                    }
                } ?: evalH?.statusText ?: "Belum diukur"

                val headEval = MetricEvaluation(
                    valueFormatted = if (headCm != null) String.format(Locale("id", "ID"), "%.1f", headCm) else "-",
                    unit = "cm",
                    percentileText = officialHeadPercentile,
                    statusText = officialHeadStatus,
                    isMeasured = headCm != null
                )

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
                    officialWeightPercentile.isNotEmpty() && officialWeightPercentile != "-" -> {
                        val pctNum = officialWeightPercentile.removePrefix("P").toIntOrNull() ?: 50
                        when {
                            pctNum in 10..90 -> "Sesuai Kurva Fenton (AGA)"
                            pctNum < 10 -> "Perlu Pemantauan (SGA < P10)"
                            else -> "Besar untuk Usia Gestasi (LGA)"
                        }
                    }
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
