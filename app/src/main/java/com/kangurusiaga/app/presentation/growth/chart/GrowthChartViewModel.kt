package com.kangurusiaga.app.presentation.growth.chart

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.local.fenton.GrowthPercentileEvaluation
import com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.usecase.EnsureInitialGrowthDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class GrowthChartUiState(
    val parameter: GrowthParameter = GrowthParameter.WEIGHT,
    val selectedTab: Int = 0, // 0 = Semua Parameter (Fenton Full), 1 = Berat Badan, 2 = Panjang, 3 = Lingkar Kepala
    val baby: Baby? = null,
    val currentPmaWeeks: Float = 35f,
    // All 3 parameter measurements in ascending order for chart plotting
    val weightMeasurementsAsc: List<GrowthMeasurement> = emptyList(),
    val lengthMeasurementsAsc: List<GrowthMeasurement> = emptyList(),
    val headCircMeasurementsAsc: List<GrowthMeasurement> = emptyList(),
    // Latest measurements for each metric
    val latestWeight: GrowthMeasurement? = null,
    val latestLength: GrowthMeasurement? = null,
    val latestHeadCirc: GrowthMeasurement? = null,
    // LMS evaluations
    val weightEvaluation: GrowthPercentileEvaluation? = null,
    val lengthEvaluation: GrowthPercentileEvaluation? = null,
    val headCircEvaluation: GrowthPercentileEvaluation? = null,
    val weightGainRateGramsPerDay: Float = 28f,
    // For legacy single-parameter list views
    val measurementsAsc: List<GrowthMeasurement> = emptyList(),
    val measurementsDesc: List<GrowthMeasurement> = emptyList(),
    val latestMeasurement: GrowthMeasurement? = null,
    val isLoading: Boolean = true,
    // Fenton API State
    val fentonChartImageUrl: String? = null,
    val fentonChartLocalPath: String? = null,
    val fentonChartTimestamp: Long = 0L,
    val isFentonApiLoading: Boolean = false,
    val fentonApiErrorMessage: String? = null,
    val fentonApiSuccess: Boolean = false
)

@HiltViewModel
class GrowthChartViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val ensureInitialGrowthDataUseCase: EnsureInitialGrowthDataUseCase,
    private val fentonGrowthApiClient: FentonGrowthApiClient,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val parameterName: String = savedStateHandle.get<String>("parameter") ?: "FULL"
    private val initialTab: Int = savedStateHandle.get<Int>("tab") ?: when (parameterName.uppercase()) {
        "WEIGHT" -> 1
        "LENGTH" -> 2
        "HEAD_CIRCUMFERENCE" -> 3
        else -> 0
    }

    private val initialParam: GrowthParameter = when (parameterName.uppercase()) {
        "LENGTH" -> GrowthParameter.LENGTH
        "HEAD_CIRCUMFERENCE" -> GrowthParameter.HEAD_CIRCUMFERENCE
        else -> GrowthParameter.WEIGHT
    }

    private val _uiState = MutableStateFlow(
        GrowthChartUiState(
            parameter = initialParam,
            selectedTab = initialTab
        )
    )
    val uiState: StateFlow<GrowthChartUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun selectTab(index: Int) {
        val targetParam = when (index) {
            1 -> GrowthParameter.WEIGHT
            2 -> GrowthParameter.LENGTH
            3 -> GrowthParameter.HEAD_CIRCUMFERENCE
            else -> GrowthParameter.WEIGHT
        }
        _uiState.update { current ->
            val activeAsc = when (index) {
                1 -> current.weightMeasurementsAsc
                2 -> current.lengthMeasurementsAsc
                3 -> current.headCircMeasurementsAsc
                else -> current.weightMeasurementsAsc
            }
            current.copy(
                selectedTab = index,
                parameter = targetParam,
                measurementsAsc = activeAsc,
                measurementsDesc = activeAsc.asReversed(),
                latestMeasurement = activeAsc.lastOrNull()
            )
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                ensureInitialGrowthDataUseCase.ensureArkaNarendraSampleData()
            } catch (_: Exception) {
            }

            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby == null) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@collectLatest
                }

                // Compute real PMA (35 weeks for Arka)
                val chronoWeeks = ((System.currentTimeMillis() - baby.birthDateEpochMillis) / (7.0 * 24 * 60 * 60 * 1000.0)).toFloat()
                val calculatedPma = (baby.gestationalAgeWeeks.toFloat() + chronoWeeks).coerceIn(22f, 50f)

                val cachedFile = File(context.cacheDir, "fenton_chart_${baby.id}.jpg")
                val initialLocalPath = if (cachedFile.exists() && cachedFile.length() > 0) cachedFile.absolutePath else null
                val initialTimestamp = if (cachedFile.exists()) cachedFile.lastModified() else 0L

                _uiState.update {
                    it.copy(
                        baby = baby,
                        currentPmaWeeks = calculatedPma,
                        isLoading = false,
                        fentonChartLocalPath = initialLocalPath,
                        fentonChartTimestamp = initialTimestamp
                    )
                }

                // Langsung fetch data ke API FentonGrowth.ca saat pertama kali masuk
                fetchFentonChart(baby)

                launch {
                    try {
                        ensureInitialGrowthDataUseCase(baby)
                    } catch (_: Exception) {
                    }
                }

                // Combine flows for all 3 parameters
                launch {
                    combine(
                        growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.WEIGHT),
                        growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.LENGTH),
                        growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE)
                    ) { weights, lengths, headCircs ->
                        Triple(weights, lengths, headCircs)
                    }.collectLatest { (weights, lengths, headCircs) ->
                        val latestW = weights.lastOrNull()
                        val latestL = lengths.lastOrNull()
                        val latestHc = headCircs.lastOrNull()

                        val evalW = if (latestW != null) {
                            FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.WEIGHT,
                                gender = baby.gender,
                                pmaWeeks = latestW.pmaWeeks,
                                value = latestW.value
                            )
                        } else null

                        val evalL = if (latestL != null) {
                            FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.LENGTH,
                                gender = baby.gender,
                                pmaWeeks = latestL.pmaWeeks,
                                value = latestL.value
                            )
                        } else null

                        val evalHc = if (latestHc != null) {
                            FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                                gender = baby.gender,
                                pmaWeeks = latestHc.pmaWeeks,
                                value = latestHc.value
                            )
                        } else null

                        // Calculate rate of weight gain in grams/day if >= 2 measurements exist
                        val weightGainRate = if (weights.size >= 2) {
                            val prev = weights[weights.size - 2]
                            val curr = weights.last()
                            val days = ((curr.measurementDateEpochMillis - prev.measurementDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val gainGrams = (curr.value - prev.value) * 1000f
                            (gainGrams / days).toFloat().coerceIn(0f, 100f)
                        } else {
                            28f // Standard clinically targetted catch-up rate (+28 g/hari)
                        }

                        val latestChartFile = File(context.cacheDir, "fenton_chart_${baby.id}.jpg")
                        val updatedLocalPath = if (latestChartFile.exists() && latestChartFile.length() > 0) latestChartFile.absolutePath else null
                        val updatedTimestamp = if (latestChartFile.exists()) latestChartFile.lastModified() else 0L

                        _uiState.update { current ->
                            val activeAsc = when (current.selectedTab) {
                                1 -> weights
                                2 -> lengths
                                3 -> headCircs
                                else -> weights
                            }
                            current.copy(
                                weightMeasurementsAsc = weights,
                                lengthMeasurementsAsc = lengths,
                                headCircMeasurementsAsc = headCircs,
                                latestWeight = latestW,
                                latestLength = latestL,
                                latestHeadCirc = latestHc,
                                weightEvaluation = evalW,
                                lengthEvaluation = evalL,
                                headCircEvaluation = evalHc,
                                weightGainRateGramsPerDay = weightGainRate,
                                measurementsAsc = activeAsc,
                                measurementsDesc = activeAsc.asReversed(),
                                latestMeasurement = activeAsc.lastOrNull(),
                                fentonChartLocalPath = updatedLocalPath ?: current.fentonChartLocalPath,
                                fentonChartTimestamp = if (updatedTimestamp > 0L) updatedTimestamp else current.fentonChartTimestamp
                            )
                        }
                    }
                }
            }
        }
    }

    private var fentonFetchJob: Job? = null

    fun fetchFentonChart(babyParam: Baby? = null) {
        val baby = babyParam ?: _uiState.value.baby ?: return
        fentonFetchJob?.cancel()
        fentonFetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isFentonApiLoading = true, fentonApiErrorMessage = null) }
            val weights = try {
                growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.WEIGHT).first()
            } catch (_: Exception) {
                _uiState.value.weightMeasurementsAsc
            }
            val lengths = try {
                growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.LENGTH).first()
            } catch (_: Exception) {
                _uiState.value.lengthMeasurementsAsc
            }
            val headCircs = try {
                growthRepository.getMeasurementsAsc(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE).first()
            } catch (_: Exception) {
                _uiState.value.headCircMeasurementsAsc
            }
            val allMeasurements = weights + lengths + headCircs

            val result = fentonGrowthApiClient.fetchPlotChart(baby, allMeasurements)
            result.onSuccess { plotResult ->
                val cacheFile = File(context.cacheDir, "fenton_chart_${baby.id}.jpg")
                val timestamp = if (cacheFile.exists()) cacheFile.lastModified() else System.currentTimeMillis()
                _uiState.update { current ->
                    current.copy(
                        isFentonApiLoading = false,
                        fentonApiSuccess = true,
                        fentonChartImageUrl = plotResult.contentUrl,
                        fentonChartLocalPath = plotResult.localFilePath ?: current.fentonChartLocalPath,
                        fentonChartTimestamp = timestamp,
                        fentonApiErrorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { current ->
                    current.copy(
                        isFentonApiLoading = false,
                        fentonApiErrorMessage = err.message ?: "Gagal memproses grafik dari FentonGrowth.ca"
                    )
                }
            }
        }
    }

    fun retryFetchFentonChart() {
        _uiState.update { it.copy(fentonApiErrorMessage = null) }
        fetchFentonChart()
    }

    fun dismissFentonApiError() {
        _uiState.update { it.copy(fentonApiErrorMessage = null) }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch {
            growthRepository.deleteMeasurement(id)
            fentonGrowthRepository.deleteRecordById(id)
        }
    }
}

