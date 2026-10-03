package com.kangurusiaga.app.presentation.growth.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrowthChartUiState(
    val parameter: GrowthParameter = GrowthParameter.WEIGHT,
    val selectedTab: Int = 0, // 0 = Grafik, 1 = Data
    val baby: Baby? = null,
    val measurementsAsc: List<GrowthMeasurement> = emptyList(),
    val measurementsDesc: List<GrowthMeasurement> = emptyList(),
    val latestMeasurement: GrowthMeasurement? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class GrowthChartViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val parameterName: String = savedStateHandle.get<String>("parameter") ?: GrowthParameter.WEIGHT.name
    val parameter: GrowthParameter = GrowthParameter.fromName(parameterName)
    private val initialTab: Int = savedStateHandle.get<Int>("tab") ?: 0

    private val _uiState = MutableStateFlow(
        GrowthChartUiState(
            parameter = parameter,
            selectedTab = initialTab
        )
    )
    val uiState: StateFlow<GrowthChartUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    private fun loadData() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                _uiState.update { it.copy(baby = baby, isLoading = false) }
                if (baby != null) {
                    launch {
                        growthRepository.getMeasurementsAsc(baby.id, parameter).collectLatest { list ->
                            _uiState.update { it.copy(measurementsAsc = list) }
                        }
                    }
                    launch {
                        growthRepository.getMeasurementsDesc(baby.id, parameter).collectLatest { list ->
                            _uiState.update {
                                it.copy(
                                    measurementsDesc = list,
                                    latestMeasurement = list.firstOrNull()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch {
            growthRepository.deleteMeasurement(id)
            fentonGrowthRepository.deleteRecordById(id)
        }
    }
}
