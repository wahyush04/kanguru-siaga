package com.kangurusiaga.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.model.MetricType
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BabyProfileViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository
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
                val weeks = diffDays / 7
                val months = diffDays / 30

                val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                val formattedDate = dateFormatter.format(Date(baby.birthDateEpochMillis))

                val isBblr = baby.birthWeightGram < 2500
                val isPremature = baby.gestationalAgeWeeks < 37

                val pma = PmaCalculator.calculate(
                    birthDateEpochMillis = baby.birthDateEpochMillis,
                    measurementDateEpochMillis = now,
                    gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        baby = baby,
                        formattedBirthDate = formattedDate,
                        chronologicalAgeWeeks = weeks,
                        chronologicalAgeMonths = months,
                        isBblr = isBblr,
                        isPremature = isPremature
                    )
                }

                // Collect latest measurements
                observeMeasurements(baby, pma.pmaWeeks.toInt())
            }
        }
    }

    private fun observeMeasurements(baby: Baby, pmaWeeks: Int) {
        val gender = baby.gender ?: Gender.MALE

        // Weight
        viewModelScope.launch {
            growthRepository.getLatestMeasurement(baby.id, GrowthParameter.WEIGHT).collectLatest { m ->
                val weightGrams: Double = m?.value?.let { it.toDouble() * 1000.0 } ?: baby.birthWeightGram.toDouble()
                val weightKg = weightGrams / 1000.0
                val formattedVal = String.format(Locale("id", "ID"), "%.1f", weightKg)

                val eval = try {
                    fentonGrowthRepository.evaluateMeasurement(
                        metric = MetricType.WEIGHT,
                        gender = gender,
                        pmaWeeks = pmaWeeks,
                        value = weightGrams
                    )
                } catch (e: Exception) {
                    null
                }

                val percentileText = eval?.percentileBadge ?: "Persentil P50"
                val statusText = eval?.statusText ?: "Normal"

                _uiState.update { state ->
                    state.copy(
                        weightMetric = MetricEvaluation(
                            valueFormatted = formattedVal,
                            unit = "kg",
                            percentileText = percentileText,
                            statusText = statusText
                        )
                    )
                }
            }
        }

        // Length
        viewModelScope.launch {
            growthRepository.getLatestMeasurement(baby.id, GrowthParameter.LENGTH).collectLatest { m ->
                val lengthCm: Double = m?.value?.toDouble() ?: (if (baby.birthLengthCm > 0) baby.birthLengthCm.toDouble() else 48.0)
                val formattedVal = String.format(Locale("id", "ID"), "%.1f", lengthCm)

                val eval = try {
                    fentonGrowthRepository.evaluateMeasurement(
                        metric = MetricType.LENGTH,
                        gender = gender,
                        pmaWeeks = pmaWeeks,
                        value = lengthCm
                    )
                } catch (e: Exception) {
                    null
                }

                val percentileText = eval?.percentileBadge ?: "Persentil P50"
                val statusText = eval?.statusText ?: "Normal"

                _uiState.update { state ->
                    state.copy(
                        lengthMetric = MetricEvaluation(
                            valueFormatted = formattedVal,
                            unit = "cm",
                            percentileText = percentileText,
                            statusText = statusText
                        )
                    )
                }
            }
        }

        // Head Circumference
        viewModelScope.launch {
            growthRepository.getLatestMeasurement(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE).collectLatest { m ->
                val headCm: Double = m?.value?.toDouble() ?: (if (baby.birthHeadCircumferenceCm > 0) baby.birthHeadCircumferenceCm.toDouble() else 34.0)
                val formattedVal = String.format(Locale("id", "ID"), "%.1f", headCm)

                val eval = try {
                    fentonGrowthRepository.evaluateMeasurement(
                        metric = MetricType.HEAD_CIRCUMFERENCE,
                        gender = gender,
                        pmaWeeks = pmaWeeks,
                        value = headCm
                    )
                } catch (e: Exception) {
                    null
                }

                val percentileText = eval?.percentileBadge ?: "Persentil P50"
                val statusText = eval?.statusText ?: "Normal"

                _uiState.update { state ->
                    state.copy(
                        headMetric = MetricEvaluation(
                            valueFormatted = formattedVal,
                            unit = "cm",
                            percentileText = percentileText,
                            statusText = statusText
                        )
                    )
                }
            }
        }
    }
}
