package com.kangurusiaga.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

/**
 * Tipe status untuk visual feedback toast / snackbar.
 */
enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

/**
 * Deteksi otomatis tipe toast dari isi pesan teks.
 */
fun detectToastType(message: String): ToastType {
    val lower = message.lowercase()
    return when {
        lower.contains("gagal") ||
        lower.contains("error") ||
        lower.contains("salah") ||
        lower.contains("belum") ||
        lower.contains("tidak valid") ||
        lower.contains("kesalahan") -> ToastType.ERROR

        lower.contains("info") ||
        lower.contains("perhatian") -> ToastType.INFO

        else -> ToastType.SUCCESS
    }
}

/**
 * Custom Toast / Snackbar Pill yang modern, elegan, dan kontras tinggi.
 * Tampilan pill melengkung penuh dengan ikon status bulat dan teks putih bersih.
 */
@Composable
fun KanguruSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    type: ToastType = detectToastType(message)
) {
    val colors = KanguruTheme.colors

    val (iconBg, iconTint, iconVector) = when (type) {
        ToastType.SUCCESS -> Triple(
            colors.toastSuccessIconBg,
            colors.toastSuccessIconTint,
            Icons.Default.Check
        )
        ToastType.ERROR -> Triple(
            colors.toastErrorIconBg,
            colors.toastErrorIconTint,
            Icons.Default.Close
        )
        ToastType.INFO -> Triple(
            colors.info,
            colors.toastText,
            Icons.Default.Info
        )
    }

    Surface(
        shape = CircleShape,
        color = colors.toastBackground,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .wrapContentWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Lingkaran Status Icon
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Teks Pesan Toast
            Text(
                text = message,
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = colors.toastText
            )
        }
    }
}

/**
 * Overload KanguruSnackbar untuk integrasi langsung dengan Material 3 SnackbarData.
 */
@Composable
fun KanguruSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    KanguruSnackbar(
        message = snackbarData.visuals.message,
        modifier = modifier
    )
}

/**
 * Drop-in replacement untuk [SnackbarHost] pada [androidx.compose.material3.Scaffold].
 * Mengatur posisi toast di bagian bawah tengah layar secara elegan.
 */
@Composable
fun KanguruSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) { snackbarData ->
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            KanguruSnackbar(snackbarData = snackbarData)
        }
    }
}
