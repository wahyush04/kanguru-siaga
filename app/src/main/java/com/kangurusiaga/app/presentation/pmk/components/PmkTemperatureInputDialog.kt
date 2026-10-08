package com.kangurusiaga.app.presentation.pmk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import java.util.Locale
import kotlin.math.round

/**
 * Modal Dialog: Input Suhu Tubuh Bayi
 * Single Source of Truth UI: Google Stitch "Kanguru Siaga - Input Suhu Tubuh Bayi"
 * Memudahkan orang tua memilih suhu tubuh bayi secara visual dan akurat (32.0°C - 42.0°C)
 * tanpa perlu mengetik angka desimal manual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkTemperatureInputDialog(
    initialTemperature: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    val parsedInitial = initialTemperature.toFloatOrNull() ?: 36.8f
    var tempValue by remember {
        mutableFloatStateOf(parsedInitial.coerceIn(32.0f, 42.0f))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.surface,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp,
            modifier = modifier
                .fillMaxWidth()
                .widthIn(max = 340.dp)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // =========================================================================
                // 1. HEADER (Icon + Judul + Tombol Tutup)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Termometer",
                                tint = colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Input Suhu Tubuh Bayi",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pantau berkala cegah hipotermia BBLR",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textTertiary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.tempCancelBg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = colors.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =========================================================================
                // 2. STATUS ZONE BADGE
                // =========================================================================
                val roundedTemp = round(tempValue * 10f) / 10f
                val (badgeIcon, badgeText, badgeBg, badgeTextColor, sublabel, sublabelColor) = when {
                    roundedTemp in 36.5f..37.5f -> ZoneInfo(
                        icon = Icons.Default.CheckCircle,
                        text = "36.5°C – 37.5°C Suhu Normal & Optimal",
                        bg = colors.tempNormalBadgeBg,
                        textColor = colors.tempNormalText,
                        sublabel = "Suhu Nyaman Dekapan Ibu",
                        sublabelColor = colors.tempNormalText
                    )
                    roundedTemp < 36.5f -> ZoneInfo(
                        icon = Icons.Default.Info,
                        text = "< 36.5°C Bayi Hipotermia (Dingin)",
                        bg = colors.infoContainer,
                        textColor = colors.tempColdText,
                        sublabel = "Waspada: Dekap PMK Lebih Rapat",
                        sublabelColor = colors.tempColdText
                    )
                    else -> ZoneInfo(
                        icon = Icons.Default.Warning,
                        text = "> 37.5°C Bayi Demam / Hangat",
                        bg = colors.errorContainer,
                        textColor = colors.tempWarmText,
                        sublabel = "Cek Ventilasi & Konsultasikan",
                        sublabelColor = colors.tempWarmText
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = badgeTextColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = badgeText,
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =========================================================================
                // 3. STEPPER CONTROLS & TEMPERATURE VALUE DISPLAY
                // =========================================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Minus (-)
                    Surface(
                        onClick = {
                            val next = (roundedTemp - 0.1f).coerceIn(32.0f, 42.0f)
                            tempValue = round(next * 10f) / 10f
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = colors.tempStepperBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.tempStepperBorder),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Kurangi Suhu",
                                tint = colors.tempStepperIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Value & Sublabel Display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.1f", roundedTemp),
                                style = KanguruTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "°C",
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textTertiary,
                                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                            )
                        }
                        Text(
                            text = sublabel,
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = sublabelColor
                        )
                    }

                    // Tombol Plus (+)
                    Surface(
                        onClick = {
                            val next = (roundedTemp + 0.1f).coerceIn(32.0f, 42.0f)
                            tempValue = round(next * 10f) / 10f
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = colors.tempStepperBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.tempStepperBorder),
                        shadowElevation = 3.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Suhu",
                                tint = colors.tempStepperIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // =========================================================================
                // 4. SLIDER & COLOR SCALE BAR (32.0°C - 42.0°C)
                // =========================================================================
                Slider(
                    value = tempValue,
                    onValueChange = {
                        tempValue = round(it * 10f) / 10f
                    },
                    valueRange = 32.0f..42.0f,
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                        )
                    },
                    track = { _ ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.tempSliderTrack)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Color Segments Bar (32.0 - 42.0)
                // Dingin: 32.0 - 36.5 (4.5 / 10.0 = 45.0%)
                // Normal: 36.5 - 37.5 (1.0 / 10.0 = 10.0%)
                // Demam: 37.5 - 42.0 (4.5 / 10.0 = 45.0%)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.450f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                            .background(colors.tempColdTrack)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .weight(0.100f)
                            .height(6.dp)
                            .background(colors.tempNormalTrack)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .weight(0.450f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                            .background(colors.tempWarmTrack)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Labels scale
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "32.0° (Dingin)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tempColdText
                    )
                    Text(
                        text = "36.5° - 37.5°",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tempNormalText
                    )
                    Text(
                        text = "42.0° (Demam)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tempWarmText
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 5. ACTION BUTTONS (Batal & Simpan)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol Batal
                    Surface(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        color = colors.tempCancelBg,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Batal",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.tempCancelText
                            )
                        }
                    }

                    // Tombol Simpan
                    Surface(
                        onClick = {
                            val formatted = String.format(Locale.US, "%.1f", roundedTemp)
                            onConfirm(formatted)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = colors.primary,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Simpan",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ZoneInfo(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val text: String,
    val bg: Color,
    val textColor: Color,
    val sublabel: String,
    val sublabelColor: Color
)
