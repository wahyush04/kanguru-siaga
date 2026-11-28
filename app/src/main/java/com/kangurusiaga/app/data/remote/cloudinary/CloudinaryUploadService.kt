package com.kangurusiaga.app.data.remote.cloudinary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.kangurusiaga.app.BuildConfig
import com.kangurusiaga.app.core.common.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

data class CloudinaryUploadResult(
    val publicId: String,
    val secureUrl: String,
    val folder: String,
    val type: String
)

/**
 * Service untuk mengunggah foto verifikasi posisi PMK secara privat (signed upload)
 * ke folder khusus Cloudinary.
 */
@Singleton
class CloudinaryUploadService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "CloudinaryUploadService"
        const val PMK_VERIFICATION_FOLDER = "kanguru-siaga/verifikasi-pmk"
        private const val ASSET_TYPE_PRIVATE = "private"
        private const val CONNECT_TIMEOUT_MS = 20_000
        private const val READ_TIMEOUT_MS = 30_000
        private const val MAX_DIMENSION_PX = 1600
        private const val JPEG_QUALITY = 85
        private const val LINE_FEED = "\r\n"

        /**
         * Menghitung SHA-1 signature sesuai spesifikasi Cloudinary Signed Upload:
         * parameter yang ditandatangani diurutkan alfabetis (`folder`, `public_id`, `timestamp`, `type`),
         * lalu digabung dengan api_secret tanpa separator.
         */
        fun generateSignature(
            params: Map<String, String>,
            apiSecret: String
        ): String {
            val sortedParams = params.toSortedMap()
                .entries
                .joinToString("&") { "${it.key}=${it.value}" }
            val toSign = sortedParams + apiSecret
            val digest = MessageDigest.getInstance("SHA-1")
                .digest(toSign.toByteArray(StandardCharsets.UTF_8))
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    suspend fun uploadPrivatePmkVerificationPhoto(
        imageUri: Uri,
        babyId: Long
    ): Result<CloudinaryUploadResult> = withContext(Dispatchers.IO) {
        try {
            val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME
            val apiKey = BuildConfig.CLOUDINARY_API_KEY
            val apiSecret = BuildConfig.CLOUDINARY_API_SECRET

            if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Konfigurasi Cloudinary belum lengkap di BuildConfig.")
                )
            }

            val imageBytes = readAndCompressImage(imageUri)
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Gagal membaca file foto yang dipilih.")
                )

            val timestamp = (System.currentTimeMillis() / 1000L).toString()
            val publicId = "pmk_pos_${babyId}_$timestamp"

            val signedParams = mapOf(
                "folder" to PMK_VERIFICATION_FOLDER,
                "public_id" to publicId,
                "timestamp" to timestamp,
                "type" to ASSET_TYPE_PRIVATE
            )
            val signature = generateSignature(signedParams, apiSecret)

            val endpoint = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"
            val boundary = "----KanguruCloudinaryBoundary${System.currentTimeMillis()}"
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                useCaches = false
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            connection.outputStream.use { out ->
                writeFormField(out, boundary, "api_key", apiKey)
                writeFormField(out, boundary, "folder", PMK_VERIFICATION_FOLDER)
                writeFormField(out, boundary, "public_id", publicId)
                writeFormField(out, boundary, "timestamp", timestamp)
                writeFormField(out, boundary, "type", ASSET_TYPE_PRIVATE)
                writeFormField(out, boundary, "signature", signature)
                writeFileField(out, boundary, "file", "$publicId.jpg", "image/jpeg", imageBytes)
                out.write("--$boundary--$LINE_FEED".toByteArray(StandardCharsets.UTF_8))
                out.flush()
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
            }
            connection.disconnect()

            if (responseCode !in 200..299) {
                val errMsg = parseErrorMessage(responseBody)
                    ?: "Gagal mengunggah foto (HTTP $responseCode)"
                AppLogger.e(TAG, "Cloudinary upload failed ($responseCode): $responseBody")
                return@withContext Result.failure(IllegalStateException(errMsg))
            }

            val json = JSONObject(responseBody)
            val result = CloudinaryUploadResult(
                publicId = json.optString("public_id", publicId),
                secureUrl = json.optString("secure_url"),
                folder = json.optString("folder", PMK_VERIFICATION_FOLDER),
                type = json.optString("type", ASSET_TYPE_PRIVATE)
            )
            AppLogger.d(TAG, "Cloudinary private upload success: publicId=${result.publicId}, type=${result.type}")
            Result.success(result)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Cloudinary upload exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun readAndCompressImage(uri: Uri): ByteArray? {
        val rawBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return null

        return try {
            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, boundsOpts)
            val width = boundsOpts.outWidth
            val height = boundsOpts.outHeight
            if (width <= 0 || height <= 0) return rawBytes

            var sampleSize = 1
            while (width / sampleSize > MAX_DIMENSION_PX || height / sampleSize > MAX_DIMENSION_PX) {
                sampleSize *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size, decodeOpts)
                ?: return rawBytes

            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
            bitmap.recycle()
            output.toByteArray()
        } catch (_: Exception) {
            rawBytes
        }
    }

    private fun writeFormField(
        out: OutputStream,
        boundary: String,
        name: String,
        value: String
    ) {
        val sb = StringBuilder()
        sb.append("--").append(boundary).append(LINE_FEED)
        sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED)
        sb.append(LINE_FEED)
        sb.append(value).append(LINE_FEED)
        out.write(sb.toString().toByteArray(StandardCharsets.UTF_8))
    }

    private fun writeFileField(
        out: OutputStream,
        boundary: String,
        fieldName: String,
        fileName: String,
        mimeType: String,
        bytes: ByteArray
    ) {
        val header = StringBuilder()
        header.append("--").append(boundary).append(LINE_FEED)
        header.append("Content-Disposition: form-data; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED)
        header.append("Content-Type: ").append(mimeType).append(LINE_FEED)
        header.append(LINE_FEED)
        out.write(header.toString().toByteArray(StandardCharsets.UTF_8))
        out.write(bytes)
        out.write(LINE_FEED.toByteArray(StandardCharsets.UTF_8))
    }

    private fun parseErrorMessage(responseBody: String): String? {
        if (responseBody.isBlank()) return null
        return try {
            val json = JSONObject(responseBody)
            json.optJSONObject("error")?.optString("message")?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
