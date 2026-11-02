package com.kangurusiaga.app.presentation.growth.chart

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class GrowthChartUiState(
    val parameter: GrowthParameter = GrowthParameter.WEIGHT,
    val selectedTab: Int = 0, // 0 = Semua Parameter (Fenton Full), 1 = Berat Badan, 2 = Panjang, 3 = Lingkar Kepala
    val baby: Baby? = null,
    val currentPmaWeeks: Float = 0f,
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
    val weightGainRateGramsPerDay: Float? = null,
    val lengthGainRateCmPerWeek: Float? = null,
    val headCircGainRateCmPerWeek: Float? = null,
    // For legacy single-parameter list views
    val measurementsAsc: List<GrowthMeasurement> = emptyList(),
    val measurementsDesc: List<GrowthMeasurement> = emptyList(),
    val latestMeasurement: GrowthMeasurement? = null,
    val isLoading: Boolean = true,
    // Fenton API State
    val fentonChartImageUrl: String? = null,
    val fentonChartLocalPath: String? = null,
    val fentonChartTimestamp: Long = 0L,
    val isFentonApiLoading: Boolean = true,
    val fentonApiErrorMessage: String? = null,
    val fentonApiSuccess: Boolean = false,
    val isSavingToGallery: Boolean = false
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
                ensureInitialGrowthDataUseCase.purgeMockDataIfPresent()
            } catch (_: Exception) {
            }

            babyRepository.getActiveBaby().collectLatest { baby ->
                if (baby == null) {
                    _uiState.update { it.copy(isLoading = false, baby = null, isFentonApiLoading = false) }
                    return@collectLatest
                }

                // Compute real PMA based on baby's actual birth date and gestational age
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
                        fentonChartTimestamp = initialTimestamp,
                        isFentonApiLoading = initialLocalPath == null
                    )
                }

                // Langsung fetch data ke API FentonGrowth.ca saat pertama kali masuk dengan data riil
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

                        // Mengambil entri Z-score resmi Fenton terbaru (minggu terakhir)
                        val latestZScoreEntry = fentonGrowthApiClient.getLatestZScoreEntry(baby.id)

                        val evalW = if (latestW != null) {
                            val baseEval = FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.WEIGHT,
                                gender = baby.gender,
                                pmaWeeks = latestW.pmaWeeks,
                                value = latestW.value
                            )
                            val officialBadge = latestZScoreEntry?.weightPercentile?.let { pct ->
                                "P$pct" + when {
                                    pct in 10..90 -> " (Ideal)"
                                    pct < 10 -> " (Kecil/SGA)"
                                    else -> " (Besar/LGA)"
                                }
                            } ?: latestW.percentileBadge?.takeIf { it.isNotBlank() } ?: baseEval.percentileBadge

                            val officialClass = latestZScoreEntry?.weightPercentile?.let { pct ->
                                when {
                                    pct < 10 -> "SGA (< P10)"
                                    pct > 90 -> "LGA (> P90)"
                                    else -> "AGA (P10-P90)"
                                }
                            } ?: baseEval.clinicalClassification

                            baseEval.copy(
                                percentileBadge = officialBadge,
                                zScore = latestZScoreEntry?.weightZScore ?: baseEval.zScore,
                                clinicalClassification = officialClass
                            )
                        } else null

                        val evalL = if (latestL != null) {
                            val baseEval = FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.LENGTH,
                                gender = baby.gender,
                                pmaWeeks = latestL.pmaWeeks,
                                value = latestL.value
                            )
                            val officialBadge = latestZScoreEntry?.lengthPercentile?.let { pct ->
                                "P$pct" + if (pct in 10..90) " (Normal)" else if (pct < 10) " (Pendek)" else " (Tinggi)"
                            } ?: latestL.percentileBadge?.takeIf { it.isNotBlank() } ?: baseEval.percentileBadge

                            baseEval.copy(
                                percentileBadge = officialBadge,
                                zScore = latestZScoreEntry?.lengthZScore ?: baseEval.zScore
                            )
                        } else null

                        val evalHc = if (latestHc != null) {
                            val baseEval = FentonReferenceData.evaluatePercentile(
                                parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                                gender = baby.gender,
                                pmaWeeks = latestHc.pmaWeeks,
                                value = latestHc.value
                            )
                            val officialBadge = latestZScoreEntry?.headPercentile?.let { pct ->
                                "P$pct" + if (pct in 10..90) " (Optimal)" else if (pct < 10) " (Kecil)" else " (Besar)"
                            } ?: latestHc.percentileBadge?.takeIf { it.isNotBlank() } ?: baseEval.percentileBadge

                            baseEval.copy(
                                percentileBadge = officialBadge,
                                zScore = latestZScoreEntry?.headZScore ?: baseEval.zScore
                            )
                        } else null

                        // Calculate rate of weight gain in grams/day if >= 2 measurements exist or >= 1 measurement with birth weight
                        val weightGainRate: Float? = if (weights.size >= 2) {
                            val prev = weights[weights.size - 2]
                            val curr = weights.last()
                            val days = ((curr.measurementDateEpochMillis - prev.measurementDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val gainGrams = (curr.value - prev.value) * 1000f
                            (gainGrams / days).toFloat()
                        } else if (weights.size == 1 && baby.birthWeightGram > 0) {
                            val curr = weights.first()
                            val birthWeightKg = baby.birthWeightGram / 1000f
                            val days = ((curr.measurementDateEpochMillis - baby.birthDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            if (days > 1.0 && curr.value != birthWeightKg) {
                                val gainGrams = (curr.value - birthWeightKg) * 1000f
                                (gainGrams / days).toFloat()
                            } else null
                        } else {
                            null
                        }

                        val lengthGainRate: Float? = if (lengths.size >= 2) {
                            val prev = lengths[lengths.size - 2]
                            val curr = lengths.last()
                            val days = ((curr.measurementDateEpochMillis - prev.measurementDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val weeks = days / 7.0
                            val gainCm = curr.value - prev.value
                            (gainCm / weeks).toFloat()
                        } else if (lengths.size == 1 && baby.birthLengthCm > 0f) {
                            val curr = lengths.first()
                            val days = ((curr.measurementDateEpochMillis - baby.birthDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val weeks = days / 7.0
                            if (days > 1.0 && curr.value != baby.birthLengthCm) {
                                val gainCm = curr.value - baby.birthLengthCm
                                (gainCm / weeks).toFloat()
                            } else null
                        } else {
                            null
                        }

                        val headCircGainRate: Float? = if (headCircs.size >= 2) {
                            val prev = headCircs[headCircs.size - 2]
                            val curr = headCircs.last()
                            val days = ((curr.measurementDateEpochMillis - prev.measurementDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val weeks = days / 7.0
                            val gainCm = curr.value - prev.value
                            (gainCm / weeks).toFloat()
                        } else if (headCircs.size == 1 && baby.birthHeadCircumferenceCm > 0f) {
                            val curr = headCircs.first()
                            val days = ((curr.measurementDateEpochMillis - baby.birthDateEpochMillis) / (1000.0 * 60 * 60 * 24)).coerceAtLeast(1.0)
                            val weeks = days / 7.0
                            if (days > 1.0 && curr.value != baby.birthHeadCircumferenceCm) {
                                val gainCm = curr.value - baby.birthHeadCircumferenceCm
                                (gainCm / weeks).toFloat()
                            } else null
                        } else {
                            null
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
                                lengthGainRateCmPerWeek = lengthGainRate,
                                headCircGainRateCmPerWeek = headCircGainRate,
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

    /**
     * Menyimpan gambar grafik Fenton ke Galeri (MediaStore) perangkat.
     */
    fun saveChartToGallery(onComplete: (Boolean, String) -> Unit) {
        val state = _uiState.value
        val baby = state.baby
        if (baby == null) {
            onComplete(false, "Data bayi belum tersedia")
            return
        }

        if (state.isFentonApiLoading) {
            onComplete(false, "Grafik sedang dimuat, silakan tunggu sebentar...")
            return
        }

        val localFile = state.fentonChartLocalPath?.let { File(it) }?.takeIf { it.exists() && it.length() > 0 }
            ?: File(context.cacheDir, "fenton_chart_${baby.id}.jpg").takeIf { it.exists() && it.length() > 0 }

        val imageUrl = state.fentonChartImageUrl

        if (localFile == null && imageUrl.isNullOrBlank()) {
            onComplete(false, "Grafik belum tersedia. Silakan muat ulang grafik terlebih dahulu.")
            return
        }

        _uiState.update { it.copy(isSavingToGallery = true) }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                saveImageToMediaStore(context, localFile, imageUrl, baby.name)
            }
            _uiState.update { it.copy(isSavingToGallery = false) }
            result.onSuccess { message ->
                onComplete(true, message)
            }.onFailure { err ->
                onComplete(false, err.message ?: "Gagal menyimpan grafik ke galeri")
            }
        }
    }

    private fun saveImageToMediaStore(
        context: Context,
        sourceFile: File?,
        imageUrl: String?,
        babyName: String
    ): Result<String> {
        return try {
            val sanitizedName = babyName
                .replace("[^a-zA-Z0-9_-]".toRegex(), "_")
                .trim('_')
                .ifBlank { "Bayi" }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val displayName = "Grafik_Fenton_${sanitizedName}_$timeStamp.jpg"

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "KanguruSiaga")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return Result.failure(IOException("Gagal membuat entri gambar di Galeri"))

            try {
                resolver.openOutputStream(imageUri).use { outStream ->
                    if (outStream == null) throw IOException("Gagal membuka penyimpanan galeri")

                    if (sourceFile != null && sourceFile.exists() && sourceFile.length() > 0) {
                        sourceFile.inputStream().use { inStream ->
                            inStream.copyTo(outStream)
                        }
                    } else if (!imageUrl.isNullOrBlank()) {
                        val url = URL(imageUrl)
                        val conn = (url.openConnection() as HttpURLConnection).apply {
                            connectTimeout = 15_000
                            readTimeout = 15_000
                            requestMethod = "GET"
                        }
                        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                            conn.inputStream.use { inStream ->
                                inStream.copyTo(outStream)
                            }
                        } else {
                            throw IOException("Gagal mengunduh gambar grafik (${conn.responseCode})")
                        }
                    } else {
                        throw IOException("File gambar grafik tidak ditemukan")
                    }
                    outStream.flush()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                } else {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(imageUri.path ?: ""),
                        arrayOf("image/jpeg"),
                        null
                    )
                }

                Result.success("Grafik Fenton berhasil disimpan ke Galeri")
            } catch (e: Exception) {
                try {
                    resolver.delete(imageUri, null, null)
                } catch (_: Exception) {}
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

