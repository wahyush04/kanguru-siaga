package com.kangurusiaga.app.presentation.growth.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
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
import javax.inject.Inject

data class GrowthSummaryUiState(
    val baby: Baby? = null,
    val chronologicalAgeDisplay: String = "",
    val latestWeight: GrowthMeasurement? = null,
    val latestLength: GrowthMeasurement? = null,
    val latestHead: GrowthMeasurement? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class GrowthSummaryViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthSummaryUiState())
    val uiState: StateFlow<GrowthSummaryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                val ageDisplay = if (baby != null) {
                    val calc = PmaCalculator.calculate(
                        birthDateEpochMillis = baby.birthDateEpochMillis,
                        measurementDateEpochMillis = System.currentTimeMillis(),
                        gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                    )
                    "Usia ${calc.chronologicalWeeks.toInt()} minggu (koreksi)"
                } else ""

                _uiState.update {
                    it.copy(
                        baby = baby,
                        chronologicalAgeDisplay = ageDisplay,
                        isLoading = false
                    )
                }

                if (baby != null) {
                    launch {
                        growthRepository.getLatestMeasurement(baby.id, GrowthParameter.WEIGHT)
                            .collectLatest { m -> _uiState.update { it.copy(latestWeight = m) } }
                    }
                    launch {
                        growthRepository.getLatestMeasurement(baby.id, GrowthParameter.LENGTH)
                            .collectLatest { m -> _uiState.update { it.copy(latestLength = m) } }
                    }
                    launch {
                        growthRepository.getLatestMeasurement(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE)
                            .collectLatest { m -> _uiState.update { it.copy(latestHead = m) } }
                    }
                }
            }
        }
    }
}
