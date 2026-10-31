package com.kangurusiaga.app.data.remote.fenton

import android.content.Context
import com.kangurusiaga.app.BuildConfig
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class FentonPlotResult(
    val ok: Boolean,
    val type: String,
    val filename: String,
    val contentUrl: String,
    val localFilePath: String? = null,
    val zScoreLocalPath: String? = null
)

data class FentonZScoreEntry(
    val dateString: String? = null,
    val gaWeeks: Float,
    val weightGrams: Double? = null,
    val weightZScore: Double? = null,
    val weightChangeZ: Double? = null,
    val weightPercentile: Int? = null,
    val headCm: Double? = null,
    val headZScore: Double? = null,
    val headChangeZ: Double? = null,
    val headPercentile: Int? = null,
    val lengthCm: Double? = null,
    val lengthZScore: Double? = null,
    val lengthChangeZ: Double? = null,
    val lengthPercentile: Int? = null
)

@Singleton
class FentonGrowthApiClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fentonGrowthRepository: dagger.Lazy<FentonGrowthRepository>,
    private val growthRepository: dagger.Lazy<GrowthRepository>
) {
    companion object {
        private const val BASE_URL = "https://fentongrowth.ca"
        private const val CLIENT_PLOT_ENDPOINT = "$BASE_URL/api/Fenton/ClientPlotPoints"
        private const val CLIENT_CSV_ENDPOINT = "$BASE_URL/api/Fenton/ClientDownloadCsv"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        private const val CONNECT_TIMEOUT_MS = 20_000
        private const val READ_TIMEOUT_MS = 25_000
        private const val LINE_FEED = "\r\n"
    }

    /**
     * Mengambil grafik Fenton resmi langsung dari FentonGrowth.ca melalui Client API.
     * Setelah berhasil mengunduh gambar grafik, secara otomatis memicu pengunduhan dan penyimpanan
     * kalkulasi Z-score & Persentil ke penyimpanan lokal (File CSV & Room Database).
     */
    suspend fun fetchPlotChart(
        baby: Baby,
        measurements: List<GrowthMeasurement> = emptyList(),
        runMode: String = "jpg"
    ): Result<FentonPlotResult> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.FENTON_API_KEY
            if (apiKey.isBlank()) {
                AppLogger.e("FentonGrowthApiClient", "FENTON_API_KEY belum dikonfigurasi di local.properties atau BuildConfig!")
                return@withContext Result.failure(
                    IllegalStateException("Kunci API Fenton belum disetel. Tambahkan 'fenton.api.key' pada local.properties.")
                )
            }
            AppLogger.d("FentonGrowthApiClient", "Memulai fetch ClientPlotPoints untuk baby: ${baby.name} (${baby.gender})")

            // 1. Format data ke CSV terstandarisasi Fenton
            val csvContent = buildFentonCsv(baby, measurements)
            AppLogger.d("FentonGrowthApiClient", "CSV Request yang dikirim:\n$csvContent")

            // 2. Eksekusi POST multipart/form-data ke endpoint /api/Fenton/ClientPlotPoints
            val boundary = "----WebKitFormBoundaryKanguru" + System.currentTimeMillis()
            val plotUrl = URL(CLIENT_PLOT_ENDPOINT)
            val connection = (plotUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
                doOutput = true
                useCaches = false
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "*/*")
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            val outputStream: OutputStream = connection.outputStream
            val multipartBody = buildPlotMultipartPayload(boundary, runMode, csvContent)
            outputStream.write(multipartBody)
            outputStream.flush()
            outputStream.close()

            val statusCode = connection.responseCode
            AppLogger.d("FentonGrowthApiClient", "ClientPlotPoints response code: $statusCode")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                connection.disconnect()
                AppLogger.d("FentonGrowthApiClient", "ClientPlotPoints response text: $responseText")

                val json = JSONObject(responseText)
                val ok = json.optBoolean("ok", false)
                val type = json.optString("type", "jpg")
                val filename = json.optString("filename", "fenton_growth_chart.jpg")
                val contentUrl = json.optString("contentUrl", "")

                if (!ok || contentUrl.isBlank()) {
                    return@withContext Result.failure(
                        IllegalStateException("Server Fenton tidak mengembalikan URL grafik yang valid.")
                    )
                }

                val fullImageUrl = if (contentUrl.startsWith("http://") || contentUrl.startsWith("https://")) {
                    contentUrl
                } else {
                    "$BASE_URL$contentUrl"
                }

                // 3. Unduh dan simpan gambar grafik ke file cache lokal
                val localImageFile = downloadAndCacheImage(fullImageUrl, baby.id)

                // 4. Memicu fetch /api/Fenton/ClientDownloadCsv setelah ClientPlotPoints berhasil
                var zScoreFilePath: String? = null
                try {
                    val zScoreResult = fetchAndSaveZScoreCsv(baby, csvContent)
                    zScoreFilePath = zScoreResult.getOrNull()?.absolutePath
                } catch (e: Exception) {
                    AppLogger.w("FentonGrowthApiClient", "Peringatan: Gagal auto-fetch Z-score CSV: ${e.message}")
                }

                Result.success(
                    FentonPlotResult(
                        ok = true,
                        type = type,
                        filename = filename,
                        contentUrl = fullImageUrl,
                        localFilePath = localImageFile?.absolutePath,
                        zScoreLocalPath = zScoreFilePath
                    )
                )
            } else {
                val errorText = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
                    ?: "HTTP Error $statusCode"
                connection.disconnect()
                AppLogger.e("FentonGrowthApiClient", "Gagal memanggil ClientPlotPoints: $statusCode - $errorText")
                Result.failure(
                    IllegalStateException("Server Fenton menolak permintaan ($statusCode): $errorText")
                )
            }
        } catch (e: java.net.UnknownHostException) {
            AppLogger.e("FentonGrowthApiClient", "Tidak ada koneksi internet", e)
            Result.failure(
                IllegalStateException("Tidak ada koneksi internet. Pastikan perangkat Anda terhubung ke internet untuk memuat grafik Fenton.", e)
            )
        } catch (e: java.net.SocketTimeoutException) {
            AppLogger.e("FentonGrowthApiClient", "Koneksi timeout", e)
            Result.failure(
                IllegalStateException("Koneksi ke FentonGrowth.ca batas waktu habis (timeout). Silakan periksa jaringan dan coba lagi.", e)
            )
        } catch (e: Exception) {
            AppLogger.e("FentonGrowthApiClient", "Terjadi error tidak terduga saat fetch grafik", e)
            Result.failure(
                IllegalStateException("Terjadi kendala saat memproses grafik: ${e.localizedMessage ?: "Kesalahan server"}", e)
            )
        }
    }

    /**
     * Memanggil /api/Fenton/ClientDownloadCsv, menyimpan berkas CSV ke local storage,
     * mem-parsing data Z-score & Persentil, dan menyimpannya ke Room Database lokal.
     */
    suspend fun fetchAndSaveZScoreCsv(
        baby: Baby,
        csvContent: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.FENTON_API_KEY
            if (apiKey.isBlank()) {
                AppLogger.e("FentonGrowthApiClient", "FENTON_API_KEY belum dikonfigurasi saat unduh CSV!")
                return@withContext Result.failure(
                    IllegalStateException("Kunci API Fenton belum dikonfigurasi. Tambahkan 'fenton.api.key' pada local.properties.")
                )
            }
            AppLogger.d("FentonGrowthApiClient", "Memulai fetch ClientDownloadCsv untuk baby: ${baby.name}")

            val boundary = "----WebKitFormBoundaryKanguruZ" + System.currentTimeMillis()
            val csvUrl = URL(CLIENT_CSV_ENDPOINT)
            val connection = (csvUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
                doOutput = true
                useCaches = false
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "*/*")
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            val outputStream: OutputStream = connection.outputStream
            val multipartBody = buildCsvOnlyMultipartPayload(boundary, csvContent)
            outputStream.write(multipartBody)
            outputStream.flush()
            outputStream.close()

            val statusCode = connection.responseCode
            AppLogger.d("FentonGrowthApiClient", "ClientDownloadCsv response code: $statusCode")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val csvResponseText = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                connection.disconnect()

                // 1. Simpan berkas CSV mentah ke Local Storage aplikasi
                val targetFile = File(context.filesDir, "fenton_zscore_${baby.id}.csv")
                targetFile.writeText(csvResponseText, StandardCharsets.UTF_8)
                AppLogger.d("FentonGrowthApiClient", "Berhasil menyimpan file Z-score CSV ke local storage: ${targetFile.absolutePath} (${targetFile.length()} bytes)")

                // 2. Parsing isi CSV dan masukkan ke basis data Room lokal
                val parsedEntries = parseZScoreCsv(csvResponseText)
                if (parsedEntries.isNotEmpty()) {
                    saveZScoreEntriesToDatabase(baby, parsedEntries)
                }

                Result.success(targetFile)
            } else {
                val errorText = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
                    ?: "HTTP Error $statusCode"
                connection.disconnect()
                AppLogger.w("FentonGrowthApiClient", "ClientDownloadCsv gagal: $statusCode - $errorText")
                Result.failure(IllegalStateException("Server Fenton menolak unduhan Z-score CSV ($statusCode): $errorText"))
            }
        } catch (e: Exception) {
            AppLogger.w("FentonGrowthApiClient", "Exception saat fetch Z-score CSV: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Mem-parsing luaran CSV dari /api/Fenton/ClientDownloadCsv ke daftar FentonZScoreEntry.
     */
    fun parseZScoreCsv(csvContent: String): List<FentonZScoreEntry> {
        val entries = mutableListOf<FentonZScoreEntry>()
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotBlank() }
        val headerIndex = lines.indexOfFirst { it.contains("GA_weeks", ignoreCase = true) }
        if (headerIndex == -1 || headerIndex >= lines.size - 1) return emptyList()

        val headers = lines[headerIndex].split(",").map { it.trim() }
        val gaIdx = headers.indexOfFirst { it.equals("GA_weeks", ignoreCase = true) }
        val weightIdx = headers.indexOfFirst { it.equals("Weight_g", ignoreCase = true) }
        val wZIdx = headers.indexOfFirst { it.equals("W_Z", ignoreCase = true) }
        val wChangeZIdx = headers.indexOfFirst { it.equals("W_changeZ", ignoreCase = true) }
        val wPctIdx = headers.indexOfFirst { it.equals("W_%", ignoreCase = true) }
        val headIdx = headers.indexOfFirst { it.equals("Head_cm", ignoreCase = true) }
        val hZIdx = headers.indexOfFirst { it.equals("H_Z", ignoreCase = true) }
        val hChangeZIdx = headers.indexOfFirst { it.equals("H_changeZ", ignoreCase = true) }
        val hPctIdx = headers.indexOfFirst { it.equals("H_%", ignoreCase = true) }
        val lengthIdx = headers.indexOfFirst { it.equals("Length_cm", ignoreCase = true) }
        val lZIdx = headers.indexOfFirst { it.equals("L_Z", ignoreCase = true) }
        val lChangeZIdx = headers.indexOfFirst { it.equals("L_changeZ", ignoreCase = true) }
        val lPctIdx = headers.indexOfFirst { it.equals("L_%", ignoreCase = true) }
        val dateIdx = headers.indexOfFirst { it.equals("Date", ignoreCase = true) }

        fun getDouble(parts: List<String>, idx: Int): Double? {
            if (idx < 0 || idx >= parts.size) return null
            return parts[idx].trim().toDoubleOrNull()
        }

        fun getInt(parts: List<String>, idx: Int): Int? {
            if (idx < 0 || idx >= parts.size) return null
            return parts[idx].trim().toIntOrNull()
        }

        for (i in (headerIndex + 1) until lines.size) {
            val parts = lines[i].split(",")
            if (parts.size <= gaIdx || gaIdx < 0) continue
            val ga = parts[gaIdx].trim().toFloatOrNull() ?: continue

            entries.add(
                FentonZScoreEntry(
                    dateString = if (dateIdx >= 0 && dateIdx < parts.size) parts[dateIdx].trim().ifBlank { null } else null,
                    gaWeeks = ga,
                    weightGrams = getDouble(parts, weightIdx),
                    weightZScore = getDouble(parts, wZIdx),
                    weightChangeZ = getDouble(parts, wChangeZIdx),
                    weightPercentile = getInt(parts, wPctIdx),
                    headCm = getDouble(parts, headIdx),
                    headZScore = getDouble(parts, hZIdx),
                    headChangeZ = getDouble(parts, hChangeZIdx),
                    headPercentile = getInt(parts, hPctIdx),
                    lengthCm = getDouble(parts, lengthIdx),
                    lengthZScore = getDouble(parts, lZIdx),
                    lengthChangeZ = getDouble(parts, lChangeZIdx),
                    lengthPercentile = getInt(parts, lPctIdx)
                )
            )
        }
        return entries
    }

    /**
     * Memasukkan data Z-score hasil kalkulasi resmi Fenton ke dalam basis data Room lokal.
     */
    private suspend fun saveZScoreEntriesToDatabase(baby: Baby, entries: List<FentonZScoreEntry>) {
        try {
            val fentonRepo = fentonGrowthRepository.get()
            val growthRepo = growthRepository.get()

            val existingRecords = try {
                fentonRepo.getPatientRecords(baby.id).first()
            } catch (_: Exception) {
                emptyList()
            }

            val existingWeights = try {
                growthRepo.getMeasurementsAsc(baby.id, GrowthParameter.WEIGHT).first()
            } catch (_: Exception) {
                emptyList()
            }

            val existingLengths = try {
                growthRepo.getMeasurementsAsc(baby.id, GrowthParameter.LENGTH).first()
            } catch (_: Exception) {
                emptyList()
            }

            val existingHeadCircs = try {
                growthRepo.getMeasurementsAsc(baby.id, GrowthParameter.HEAD_CIRCUMFERENCE).first()
            } catch (_: Exception) {
                emptyList()
            }

            for (entry in entries) {
                val pmaWeeks = entry.gaWeeks.toInt().coerceIn(22, 50)

                // 1. Simpan/perbarui ke baby_growth_records (Fenton record)
                val existingRec = existingRecords.find { it.pmaWeeks == pmaWeeks }
                val classification = entry.weightPercentile?.let { pct ->
                    when {
                        pct < 10 -> "SGA (< P10)"
                        pct > 90 -> "LGA (> P90)"
                        else -> "AGA (P10-P90)"
                    }
                }
                val recordEntity = if (existingRec != null) {
                    existingRec.copy(
                        weightGrams = entry.weightGrams ?: existingRec.weightGrams,
                        lengthCm = entry.lengthCm ?: existingRec.lengthCm,
                        headCircumferenceCm = entry.headCm ?: existingRec.headCircumferenceCm,
                        calculatedWeightZScore = entry.weightZScore ?: existingRec.calculatedWeightZScore,
                        calculatedWeightPercentile = entry.weightPercentile?.let { "P$it" } ?: existingRec.calculatedWeightPercentile,
                        clinicalClassification = classification ?: existingRec.clinicalClassification
                    )
                } else {
                    GrowthRecordEntity(
                        babyId = baby.id,
                        recordedAt = System.currentTimeMillis(),
                        pmaWeeks = pmaWeeks,
                        weightGrams = entry.weightGrams,
                        lengthCm = entry.lengthCm,
                        headCircumferenceCm = entry.headCm,
                        calculatedWeightZScore = entry.weightZScore,
                        calculatedWeightPercentile = entry.weightPercentile?.let { "P$it" },
                        clinicalClassification = classification
                    )
                }
                fentonRepo.saveNewRecord(recordEntity)

                // 2. Perbarui badge persentil pada growth_measurements (Weight, Length, Head)
                if (entry.weightPercentile != null) {
                    val wMeas = existingWeights.find { it.pmaWeeks.toInt() == pmaWeeks }
                    if (wMeas != null) {
                        val badge = "P${entry.weightPercentile}" + when {
                            entry.weightPercentile in 10..90 -> " (Ideal)"
                            entry.weightPercentile < 10 -> " (Kecil/SGA)"
                            else -> " (Besar/LGA)"
                        }
                        growthRepo.updateMeasurement(wMeas.copy(percentileBadge = badge, updatedAt = System.currentTimeMillis()))
                    }
                }

                if (entry.lengthPercentile != null) {
                    val lMeas = existingLengths.find { it.pmaWeeks.toInt() == pmaWeeks }
                    if (lMeas != null) {
                        val badge = "P${entry.lengthPercentile} (Normal)"
                        growthRepo.updateMeasurement(lMeas.copy(percentileBadge = badge, updatedAt = System.currentTimeMillis()))
                    }
                }

                if (entry.headPercentile != null) {
                    val hMeas = existingHeadCircs.find { it.pmaWeeks.toInt() == pmaWeeks }
                    if (hMeas != null) {
                        val badge = "P${entry.headPercentile} (Optimal)"
                        growthRepo.updateMeasurement(hMeas.copy(percentileBadge = badge, updatedAt = System.currentTimeMillis()))
                    }
                }
            }
            AppLogger.d("FentonGrowthApiClient", "Sukses memperbarui ${entries.size} record Z-score ke database lokal")
        } catch (e: Exception) {
            AppLogger.w("FentonGrowthApiClient", "Gagal memperbarui database Z-score: ${e.message}")
        }
    }

    /**
     * Memeriksa berkas CSV Z-score lokal dari penyimpanan perangkat.
     */
    fun getLocalZScoreCsvFile(babyId: Long): File? {
        val file = File(context.filesDir, "fenton_zscore_${babyId}.csv")
        return if (file.exists() && file.length() > 0) file else null
    }

    /**
     * Membaca entri Z-score yang tersimpan di CSV lokal.
     */
    fun readLocalZScoreEntries(babyId: Long): List<FentonZScoreEntry> {
        val file = getLocalZScoreCsvFile(babyId) ?: return emptyList()
        return try {
            parseZScoreCsv(file.readText(StandardCharsets.UTF_8))
        } catch (e: Exception) {
            AppLogger.w("FentonGrowthApiClient", "Gagal membaca local Z-score CSV: ${e.message}")
            emptyList()
        }
    }

    /**
     * Mengambil entri Z-score resmi terbaru (minggu terakhir) dari penyimpanan lokal.
     */
    fun getLatestZScoreEntry(babyId: Long): FentonZScoreEntry? {
        return readLocalZScoreEntries(babyId).lastOrNull()
    }

    /**
     * Mengambil entri Z-score resmi untuk minggu PMA tertentu.
     */
    fun getZScoreEntryForPma(babyId: Long, pmaWeeks: Int): FentonZScoreEntry? {
        return readLocalZScoreEntries(babyId).find { it.gaWeeks.toInt() == pmaWeeks }
    }

    /**
     * Membangun payload multipart untuk ClientPlotPoints.
     */
    private fun buildPlotMultipartPayload(boundary: String, runMode: String, csvContent: String): ByteArray {
        val byteStream = ByteArrayOutputStream()

        fun writeFormField(name: String, value: String) {
            val field = "--$boundary$LINE_FEED" +
                    "Content-Disposition: form-data; name=\"$name\"$LINE_FEED$LINE_FEED" +
                    "$value$LINE_FEED"
            byteStream.write(field.toByteArray(StandardCharsets.UTF_8))
        }

        writeFormField("runMode", runMode)

        val fileHeader = "--$boundary$LINE_FEED" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"input.csv\"$LINE_FEED" +
                "Content-Type: text/csv$LINE_FEED$LINE_FEED"
        byteStream.write(fileHeader.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(csvContent.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(LINE_FEED.toByteArray(StandardCharsets.UTF_8))

        val endBoundary = "--$boundary--$LINE_FEED"
        byteStream.write(endBoundary.toByteArray(StandardCharsets.UTF_8))

        return byteStream.toByteArray()
    }

    /**
     * Membangun payload multipart untuk ClientDownloadCsv.
     */
    private fun buildCsvOnlyMultipartPayload(boundary: String, csvContent: String): ByteArray {
        val byteStream = ByteArrayOutputStream()

        val fileHeader = "--$boundary$LINE_FEED" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"input.csv\"$LINE_FEED" +
                "Content-Type: text/csv$LINE_FEED$LINE_FEED"
        byteStream.write(fileHeader.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(csvContent.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(LINE_FEED.toByteArray(StandardCharsets.UTF_8))

        val endBoundary = "--$boundary--$LINE_FEED"
        byteStream.write(endBoundary.toByteArray(StandardCharsets.UTF_8))

        return byteStream.toByteArray()
    }

    /**
     * Membangun format CSV standar masukan untuk FentonGrowth.ca.
     */
    fun buildFentonCsv(baby: Baby, measurements: List<GrowthMeasurement>): String {
        val lines = mutableListOf<String>()
        val sexCode = if (baby.gender == Gender.FEMALE) "F" else "M"

        lines.add("Sex,$sexCode")
        if (baby.name.isNotBlank()) {
            lines.add("Title,${escapeCsv(baby.name)}")
        }
        lines.add("Number,${baby.id}")

        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        val birthDateStr = sdf.format(Date(baby.birthDateEpochMillis))
        lines.add("Date of birth,$birthDateStr")

        val gaBirth = baby.gestationalAgeWeeks.coerceIn(22, 50)
        lines.add("GA at birth,$gaBirth")
        lines.add("Language,English")
        lines.add("Data Type,Growth")

        lines.add("GA (weeks),Weight (g),Head (cm),Length (cm)")

        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

        // Data awal saat lahir
        val initialWeight = baby.birthWeightGram.toDouble()
        val initialHead = if (baby.birthHeadCircumferenceCm > 0f) baby.birthHeadCircumferenceCm.toDouble() else null
        val initialLength = if (baby.birthLengthCm > 0f) baby.birthLengthCm.toDouble() else null

        data class MeasurementPoint(
            val pmaWeeks: Double,
            var weightGrams: Double? = null,
            var headCm: Double? = null,
            var lengthCm: Double? = null
        )

        val pointsMap = linkedMapOf<Int, MeasurementPoint>()

        // 1. Masukkan titik lahir onboarding
        pointsMap[gaBirth] = MeasurementPoint(
            pmaWeeks = gaBirth.toDouble(),
            weightGrams = if (initialWeight > 0) initialWeight else null,
            headCm = initialHead,
            lengthCm = initialLength
        )

        // 2. Tambahkan data pengukuran yang tersimpan di Room
        for (m in measurements) {
            val pmaInt = m.pmaWeeks.toInt().coerceIn(22, 50)
            val point = pointsMap.getOrPut(pmaInt) {
                MeasurementPoint(pmaWeeks = m.pmaWeeks.toDouble())
            }

            when (m.parameter) {
                GrowthParameter.WEIGHT -> {
                    val valGrams = if (m.value < 100f) (m.value * 1000f).toDouble() else m.value.toDouble()
                    if (valGrams > 0) point.weightGrams = valGrams
                }
                GrowthParameter.HEAD_CIRCUMFERENCE -> {
                    if (m.value > 0) point.headCm = m.value.toDouble()
                }
                GrowthParameter.LENGTH -> {
                    if (m.value > 0) point.lengthCm = m.value.toDouble()
                }
            }
        }

        // Tulis baris terurut ascending berdasarkan PMA
        val sortedPoints = pointsMap.values.sortedBy { it.pmaWeeks }
        for (pt in sortedPoints) {
            val gaStr = df.format(pt.pmaWeeks)
            val wStr = pt.weightGrams?.let { df.format(it) } ?: ""
            val hStr = pt.headCm?.let { df.format(it) } ?: ""
            val lStr = pt.lengthCm?.let { df.format(it) } ?: ""

            if (wStr.isNotBlank() || hStr.isNotBlank() || lStr.isNotBlank()) {
                lines.add("$gaStr,$wStr,$hStr,$lStr")
            }
        }

        return lines.joinToString(LINE_FEED)
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    /**
     * Mengunduh file grafik dan menyimpannya di direktori cache lokal aplikasi.
     */
    private fun downloadAndCacheImage(imageUrl: String, babyId: Long): File? {
        var connection: HttpURLConnection? = null
        return try {
            val targetFile = File(context.cacheDir, "fenton_chart_${babyId}.jpg")
            val url = URL(imageUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", USER_AGENT)
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                AppLogger.d("FentonGrowthApiClient", "Berhasil cache grafik Fenton ke: ${targetFile.absolutePath} (${targetFile.length()} bytes)")
                targetFile
            } else {
                null
            }
        } catch (e: Exception) {
            AppLogger.w("FentonGrowthApiClient", "Gagal menyimpan cache gambar lokal: ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }
}
