package com.kangurusiaga.app.presentation.growth.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

/**
 * Pop Up Konfirmasi Akses Internet & Apresiasi Dedikasi Ilmiah Fenton.
 * Desain presisi sesuai Google Stitch / panduan UI Kanguru Siaga.
 */
@Composable
fun FentonInternetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colors.surface,
                tonalElevation = 0.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // 1. Header Bar: Badge Online Cloud Integration & Tombol Tutup (X)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge Chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(colors.successContainer)
                                .border(1.dp, colors.successBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(colors.gridSubtitleTeal)
                            )
                            Text(
                                text = "Online Cloud Integration",
                                style = typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.gridSubtitleTeal
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceVariant)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = colors.closeButtonTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Icon & Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.successContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = null,
                                tint = colors.gridSubtitleTeal,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kalkulasi Pertumbuhan Fenton",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "FentonGrowth.ca (PMA 22–50 Minggu)",
                                style = typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.gridSubtitleTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Information Card (Kebutuhan Akses Internet)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surfaceVariant)
                            .padding(14.dp)
                    ) {
                        val annotatedDesc = buildAnnotatedString {
                            append("Fitur Pemantauan Pertumbuhan Bayi membutuhkan ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                append("akses internet")
                            }
                            append(" karena terintegrasi langsung secara online dengan sistem kalkulasi ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                append("FentonGrowth.ca")
                            }
                            append(" untuk menghitung persentil pertumbuhan bayi prematur (PMA 22–50 minggu) secara akurat dan terstandarisasi.")
                        }

                        Text(
                            text = annotatedDesc,
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                            lineHeight = typography.bodySmall.lineHeight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Appreciation Card (Apresiasi Dedikasi Ilmiah Dr. Tanis Fenton)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.tipBackground)
                            .border(1.dp, colors.tipBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = colors.warning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Apresiasi Dedikasi Ilmiah",
                                    style = typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.tipText
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "“Penghargaan dan apresiasi mendalam kami sampaikan kepada Dr. Tanis Fenton, PhD, RD atas dedikasi dan kontribusi ilmiah luar biasa dalam merancang kurva serta standar pertumbuhan Fenton Preterm Growth Chart yang kini menjadi pedoman klinis global.”",
                                style = typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = colors.tipText,
                                lineHeight = typography.bodySmall.lineHeight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Action Buttons (Lanjutkan ke Grafik Pertumbuhan & Batal)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tombol Utama (Gradient Coral-Pink)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(colors.pinkGradientStart, colors.pinkGradientEnd)
                                    )
                                )
                                .clickable { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Lanjutkan ke Grafik Pertumbuhan",
                                    style = typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    tint = colors.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Tombol Batal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.surfaceVariant)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Batal",
                                style = typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

