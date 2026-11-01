package com.kangurusiaga.app.core.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.kangurusiaga.app.domain.model.ScreeningType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class AttachmentResult(
    val fileName: String,
    val formattedSize: String,
    val fileUri: String,
    val isImage: Boolean
)

@Singleton
class ScreeningAttachmentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
        private const val ATTACHMENT_DIR_NAME = "screening_attachments"
    }

    private val attachmentsDir: File
        get() {
            val dir = File(context.filesDir, ATTACHMENT_DIR_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    fun createTempCameraUri(): Uri {
        val tempDir = File(context.cacheDir, "camera_photos")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val tempFile = File(tempDir, "temp_screening_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    suspend fun saveCapturedPhoto(
        sourceUri: Uri,
        babyId: Long,
        type: ScreeningType
    ): AttachmentResult = withContext(ioDispatcher) {
        val timestamp = System.currentTimeMillis()
        val typeCode = type.name.lowercase(Locale.ROOT)
        val targetFileName = "screening_${babyId}_${typeCode}_${timestamp}.jpg"
        val displayName = "Foto_${typeCode}_${timestamp}.jpg"
        val targetFile = File(attachmentsDir, targetFileName)

        val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw IllegalArgumentException("Tidak dapat membaca foto yang diambil.")

        inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        val sizeBytes = targetFile.length()
        if (sizeBytes > MAX_FILE_SIZE_BYTES) {
            targetFile.delete()
            throw IllegalArgumentException("Ukuran berkas melebihi batas maksimal 10MB.")
        }

        AttachmentResult(
            fileName = displayName,
            formattedSize = formatFileSize(sizeBytes),
            fileUri = targetFile.toURI().toString(),
            isImage = true
        )
    }

    suspend fun saveDocument(
        sourceUri: Uri,
        babyId: Long,
        type: ScreeningType
    ): AttachmentResult = withContext(ioDispatcher) {
        var originalName: String? = null
        var querySize: Long? = null

        try {
            context.contentResolver.query(sourceUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) originalName = cursor.getString(nameIndex)
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) querySize = cursor.getLong(sizeIndex)
                }
            }
        } catch (_: Exception) {
            // fallback if cursor query fails
        }

        if (querySize != null && querySize!! > MAX_FILE_SIZE_BYTES) {
            throw IllegalArgumentException("Ukuran berkas melebihi batas maksimal 10MB.")
        }

        val timestamp = System.currentTimeMillis()
        val typeCode = type.name.lowercase(Locale.ROOT)
        val safeOriginalName = originalName ?: "dokumen_${timestamp}.pdf"
        val cleanName = safeOriginalName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
        val targetFileName = "screening_${babyId}_${typeCode}_${timestamp}_$cleanName"
        val targetFile = File(attachmentsDir, targetFileName)

        val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw IllegalArgumentException("Gagal membuka berkas yang dipilih.")

        inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        val sizeBytes = targetFile.length()
        if (sizeBytes > MAX_FILE_SIZE_BYTES) {
            targetFile.delete()
            throw IllegalArgumentException("Ukuran berkas melebihi batas maksimal 10MB.")
        }

        val extension = safeOriginalName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        val isImage = extension in listOf("jpg", "jpeg", "png", "webp")

        AttachmentResult(
            fileName = safeOriginalName,
            formattedSize = formatFileSize(sizeBytes),
            fileUri = targetFile.toURI().toString(),
            isImage = isImage
        )
    }

    suspend fun deleteAttachment(fileUriString: String?): Boolean = withContext(ioDispatcher) {
        if (fileUriString == null) return@withContext false
        try {
            val uri = Uri.parse(fileUriString)
            if (uri.scheme == "file") {
                val path = uri.path ?: return@withContext false
                val file = File(path)
                if (file.exists() && file.parentFile?.name == ATTACHMENT_DIR_NAME) {
                    return@withContext file.delete()
                }
            }
            false
        } catch (_: Exception) {
            false
        }
    }

    fun getOpenAttachmentIntent(fileUriString: String): Intent? {
        try {
            val uri = Uri.parse(fileUriString)
            val file = if (uri.scheme == "file") {
                File(uri.path ?: return null)
            } else {
                return null
            }
            if (!file.exists()) return null

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val extension = file.extension.lowercase(Locale.ROOT)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: when (extension) {
                    "pdf" -> "application/pdf"
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "*/*"
                }

            return Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun getFormattedFileSize(fileUriString: String?): String? {
        if (fileUriString == null) return null
        return try {
            val uri = Uri.parse(fileUriString)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: return null)
                if (file.exists()) formatFileSize(file.length()) else null
            } else null
        } catch (_: Exception) {
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val df = DecimalFormat("#.#")
        return when {
            bytes >= 1024 * 1024 -> "${df.format(bytes / (1024.0 * 1024.0))} MB"
            bytes >= 1024 -> "${df.format(bytes / 1024.0)} KB"
            else -> "$bytes B"
        }
    }
}
