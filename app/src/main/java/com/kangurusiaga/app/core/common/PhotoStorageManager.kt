package com.kangurusiaga.app.core.common

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val photosDir: File
        get() {
            val dir = File(context.filesDir, "baby_photos")
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
        val tempFile = File(tempDir, "temp_camera_${System.currentTimeMillis()}.jpg")
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    suspend fun saveImagePermanently(sourceUri: Uri): String = withContext(ioDispatcher) {
        val targetFile = File(photosDir, "baby_photo_${System.currentTimeMillis()}.jpg")
        val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw IllegalArgumentException("Foto belum berhasil dipilih")

        inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        targetFile.toURI().toString()
    }

    suspend fun deletePhoto(photoUriString: String?): Boolean = withContext(ioDispatcher) {
        if (photoUriString == null) return@withContext false
        try {
            val uri = Uri.parse(photoUriString)
            if (uri.scheme == "file") {
                val path = uri.path ?: return@withContext false
                val file = File(path)
                if (file.exists() && file.parentFile?.name == "baby_photos") {
                    return@withContext file.delete()
                }
            }
            false
        } catch (_: Exception) {
            false
        }
    }
}
