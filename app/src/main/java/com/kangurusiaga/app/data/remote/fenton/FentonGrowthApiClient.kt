package com.kangurusiaga.app.data.remote.fenton

import android.content.Context
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

data class FentonPlotResult(
    val ok: Boolean,
    val type: String,
    val filename: String,
    val contentUrl: String,
    val localFilePath: String? = null
)

@Singleton
class FentonGrowthApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val BASE_URL = "https://fentongrowth.ca"
        private const val PLOT_ENDPOINT = "$BASE_URL/api/Fenton/PlotPoints"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        private const val CONNECT_TIMEOUT_MS = 20_000
        private const val READ_TIMEOUT_MS = 25_000
        private const val LINE_FEED = "\r\n"
    }

    /**
     * Mengambil grafik Fenton terstandarisasi langsung dari FentonGrowth.ca.
     * Mengembalikan Result berisi URL grafik dan path file cache lokal.
     */
    suspend fun fetchPlotChart(
        baby: Baby,
        measurements: List<GrowthMeasurement> = emptyList(),
        runMode: String = "jpg"
    ): Result<FentonPlotResult> = withContext(Dispatchers.IO) {
        try {
            AppLogger.d("FentonGrowthApiClient", "Memulai fetch grafik Fenton untuk baby: ${baby.name} (${baby.gender})")

            // 1. Handshake awal untuk mendapatkan Cookie & CSRF Anti-Forgery Token
            val session = acquireSecuritySession()
            if (session == null) {
                return@withContext Result.failure(
                    IllegalStateException("Tidak dapat menginisialisasi sesi keamanan dari FentonGrowth.ca. Periksa koneksi internet Anda.")
                )
            }

            // 2. Format data ke CSV terstandarisasi Fenton
            val csvContent = buildFentonCsv(baby, measurements)
            AppLogger.d("FentonGrowthApiClient", "CSV yang dihasilkan:\n$csvContent")

            // 3. Eksekusi POST multipart/form-data ke endpoint /api/Fenton/PlotPoints
            val boundary = "----WebKitFormBoundaryKanguru" + System.currentTimeMillis()
            val plotUrl = URL(PLOT_ENDPOINT)
            val connection = (plotUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
                doOutput = true
                useCaches = false
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "*/*")
                setRequestProperty("Origin", BASE_URL)
                setRequestProperty("Referer", "$BASE_URL/")
                setRequestProperty("Cookie", session.cookieHeader)
                setRequestProperty("X-CSRF-TOKEN", session.csrfToken)
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            val outputStream: OutputStream = connection.outputStream
            val multipartBody = buildMultipartPayload(boundary, runMode, csvContent)
            outputStream.write(multipartBody)
            outputStream.flush()
            outputStream.close()

            val statusCode = connection.responseCode
            AppLogger.d("FentonGrowthApiClient", "PlotPoints response code: $statusCode")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                connection.disconnect()
                AppLogger.d("FentonGrowthApiClient", "PlotPoints response text: $responseText")

                val json = JSONObject(responseText)
                val ok = json.optBoolean("ok", false)
                val type = json.optString("type", "jpg")
                val filename = json.optString("filename", "fenton_growth_chart.jpg")
                var contentUrl = json.optString("contentUrl", "")

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

                // 4. Unduh dan simpan ke cache lokal agar resilient secara offline
                val localFile = downloadAndCacheImage(fullImageUrl, baby.id)

                Result.success(
                    FentonPlotResult(
                        ok = true,
                        type = type,
                        filename = filename,
                        contentUrl = fullImageUrl,
                        localFilePath = localFile?.absolutePath
                    )
                )
            } else {
                val errorText = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
                    ?: "HTTP Error $statusCode"
                connection.disconnect()
                AppLogger.e("FentonGrowthApiClient", "Gagal memanggil Fenton API: $statusCode - $errorText")
                Result.failure(
                    IllegalStateException(
                        if (errorText.contains("Security validation failed", ignoreCase = true)) {
                            "Validasi sesi keamanan kadaluarsa. Silakan coba kembali."
                        } else {
                            "Server Fenton menolak permintaan ($statusCode): $errorText"
                        }
                    )
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
     * Membangun payload multipart/form-data.
     */
    private fun buildMultipartPayload(boundary: String, runMode: String, csvContent: String): ByteArray {
        val byteStream = ByteArrayOutputStream()

        fun writeFormField(name: String, value: String) {
            val field = "--$boundary$LINE_FEED" +
                    "Content-Disposition: form-data; name=\"$name\"$LINE_FEED$LINE_FEED" +
                    "$value$LINE_FEED"
            byteStream.write(field.toByteArray(StandardCharsets.UTF_8))
        }

        writeFormField("FromUI", "Eazy_UI")
        writeFormField("runMode", runMode)
        writeFormField("userAgent", USER_AGENT)

        // File CSV
        val fileHeader = "--$boundary$LINE_FEED" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"input.csv\"$LINE_FEED" +
                "Content-Type: text/csv$LINE_FEED$LINE_FEED"
        byteStream.write(fileHeader.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(csvContent.toByteArray(StandardCharsets.UTF_8))
        byteStream.write(LINE_FEED.toByteArray(StandardCharsets.UTF_8))

        // End boundary
        val endBoundary = "--$boundary--$LINE_FEED"
        byteStream.write(endBoundary.toByteArray(StandardCharsets.UTF_8))

        return byteStream.toByteArray()
    }

    /**
     * Mengambil cookie dan CSRF token dari halaman utama Fenton.
     */
    private fun acquireSecuritySession(): SecuritySession? {
        var connection: HttpURLConnection? = null
        return try {
            val homeUrl = URL(BASE_URL)
            connection = (homeUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            }

            // Kumpulkan Set-Cookie
            val cookies = mutableListOf<String>()
            val headerFields = connection.headerFields
            headerFields["Set-Cookie"]?.forEach { cookieHeader ->
                val cookiePart = cookieHeader.split(";").firstOrNull()?.trim()
                if (!cookiePart.isNullOrBlank()) {
                    cookies.add(cookiePart)
                }
            }
            val cookieString = cookies.joinToString("; ")

            // Baca HTML untuk ekstrak __RequestVerificationToken
            val html = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

            val csrfPattern = Pattern.compile("name=\"__RequestVerificationToken\"\\s+type=\"hidden\"\\s+value=\"([^\"]+)\"")
            val matcher = csrfPattern.matcher(html)
            val token = if (matcher.find()) {
                matcher.group(1)
            } else {
                // Pola alternatif atribut value sebelum name
                val altPattern = Pattern.compile("value=\"([^\"]+)\"\\s+name=\"__RequestVerificationToken\"")
                val altMatcher = altPattern.matcher(html)
                if (altMatcher.find()) altMatcher.group(1) else null
            }

            if (token != null && cookieString.isNotBlank()) {
                SecuritySession(cookieHeader = cookieString, csrfToken = token)
            } else {
                AppLogger.w("FentonGrowthApiClient", "Gagal menemukan token CSRF atau cookie di respon homepage")
                null
            }
        } catch (e: Exception) {
            AppLogger.e("FentonGrowthApiClient", "Error acquireSecuritySession", e)
            null
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Membangun CSV terstandarisasi untuk FentonGrowth.ca.
     */
    private fun buildFentonCsv(baby: Baby, measurements: List<GrowthMeasurement>): String {
        val lines = mutableListOf<String>()
        val sexCode = if (baby.gender == Gender.FEMALE) "F" else "M"

        lines.add("Sex,$sexCode")
        lines.add("Language,English")
        lines.add("Data Type,Growth")
        if (baby.name.isNotBlank()) {
            lines.add("Title,${escapeCsv(baby.name)}")
        }
        val gaBirth = baby.gestationalAgeWeeks.coerceIn(22, 50)
        lines.add("GA at birth,$gaBirth")

        lines.add("GA (weeks),Weight (g),Head (cm),Length (cm)")

        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

        // Data awal saat lahir (onboarding)
        val initialWeight = baby.birthWeightGram.toDouble()
        val initialHead = if (baby.birthHeadCircumferenceCm > 0f) baby.birthHeadCircumferenceCm.toDouble() else null
        val initialLength = if (baby.birthLengthCm > 0f) baby.birthLengthCm.toDouble() else null

        // Susun titik pertumbuhan gabungan
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
                    // Jika nilai < 100, berarti disimpan dalam satuan kg -> ubah ke gram
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

        // Tulis baris sorted ascending by PMA
        val sortedPoints = pointsMap.values.sortedBy { it.pmaWeeks }
        for (pt in sortedPoints) {
            val gaStr = df.format(pt.pmaWeeks)
            val wStr = pt.weightGrams?.let { df.format(it) } ?: ""
            val hStr = pt.headCm?.let { df.format(it) } ?: ""
            val lStr = pt.lengthCm?.let { df.format(it) } ?: ""

            // Minimal harus ada salah satu nilai pengukuran
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

    private data class SecuritySession(
        val cookieHeader: String,
        val csrfToken: String
    )
}

