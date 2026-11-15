package com.kangurusiaga.app.presentation.home.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlarmGuidanceBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = KanguruTheme.colors
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isAndroid14OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    val configuration = LocalConfiguration.current
    val topGap = (configuration.screenHeightDp * 0.10f).dp

    // Isolate downward scroll inside the content so that swiping down while reading
    // will NOT accidentally dismiss the bottom sheet.
    // The sheet can only be dismissed by dragging the top drag handle bar or clicking buttons.
    val contentNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Consume unconsumed downward scroll from the content so it does not propagate to ModalBottomSheet
                return if (available.y > 0f) {
                    Offset(0f, available.y)
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                // Consume downward fling velocity so fast swipes inside content don't fling the sheet closed
                return if (available.y > 0f) {
                    Velocity(0f, available.y)
                } else {
                    Velocity.Zero
                }
            }
        }
    }

    val dismissWithAnimation: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = BottomSheetDefaults.ScrimColor,
        dragHandle = null,
        shape = RectangleShape,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top 10% empty space showing scrim; tapping it dismisses the sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topGap)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = dismissWithAnimation
                    )
            )

            // 2. 90% Bottom Sheet Container Surface anchored all the way to the bottom edge
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = colors.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top drag handle touch target bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BottomSheetDefaults.DragHandle(
                            color = colors.divider.copy(alpha = 0.8f)
                        )
                    }

                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .nestedScroll(contentNestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
            // 1. Header Icon
            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.legalRoseTintBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessAlarms,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(colors.legalTealAccent)
                        .border(2.dp, colors.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Title & Subtitle
            Text(
                text = "Kunci Alarm Tepat Waktu",
                style = KanguruTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pencegah alarm tertunda akibat fitur penghemat daya bawaan sistem Android (Doze Mode).",
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Rose Callout Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.legalRoseTintBg,
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.primary)) {
                                append("Pemberian nutrisi ASI/OGT & sesi PMK bayi prematur ")
                            }
                            append("harus tepat di menit ke-0 agar tidak tertunda 2–10 menit saat ponsel terkunci.")
                        },
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Card 1: Izin "Alarm & Pengingat" (Khusus Android 14+)
            if (isAndroid14OrHigher) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = colors.cardBackground,
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "1",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Izin \"Alarm & Pengingat\" (Exact Alarm)",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.legalRoseTintBg
                            ) {
                                Text(
                                    text = "Android 14+",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = "Secara default dibatasi oleh Google. Jika belum aktif, sistem otomatis mengubah alarm menjadi inexact (dapat tertunda).",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )

                        // Breadcrumb Guide Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalSectionCardBg
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Buka Pengaturan HP:",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.textSecondary
                                    )
                                }

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    GuidancePill(text = "Aplikasi")
                                    GuidanceArrow()
                                    GuidancePill(text = "Akses khusus")
                                    GuidanceArrow()
                                    GuidancePill(text = "Alarm & pengingat")
                                    GuidanceArrow()
                                    GuidancePill(text = "Kanguru Siaga", isHighlight = true)
                                    GuidanceArrow()
                                    GuidanceActionPill(text = "Izinkan")
                                }
                            }
                        }

                        // Button to open Exact Alarm Settings
                        OutlinedButton(
                            onClick = { openExactAlarmSettings(context) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.divider),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.textPrimary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Buka Pengaturan Alarm Sekarang",
                                    style = KanguruTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. Card 2: Setel Baterai "Tidak Dibatasi" (Tampil untuk semua versi Android)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = colors.cardBackground,
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colors.legalTealAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAndroid14OrHigher) "2" else "1",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Setel Baterai \"Tidak Dibatasi\"",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalTealSurfaceBg
                        ) {
                            Text(
                                text = "Xiaomi / Oppo / Vivo",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.legalTealAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Mencegah sistem mematikan aplikasi saat layar mati lama / tidur malam di beberapa merek HP.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    // Breadcrumb Guide Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.legalSectionCardBg
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Buka Pengaturan HP:",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textSecondary
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GuidancePill(text = "Aplikasi")
                                GuidanceArrow()
                                GuidancePill(text = "Kanguru Siaga", isHighlight = true)
                                GuidanceArrow()
                                GuidancePill(text = "Baterai")
                                GuidanceArrow()
                                GuidanceActionPill(text = "Tidak Dibatasi")
                            }
                        }
                    }

                    // Button to open Battery Settings
                    OutlinedButton(
                        onClick = { openBatteryOptimizationSettings(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colors.divider),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = colors.legalTealAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Buka Pengaturan Baterai Sekarang",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            }

            // 6. Floating Fixed Bottom Action Buttons
            Surface(
                color = colors.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(
                        color = colors.divider.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = dismissWithAnimation,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Saya Sudah Mengaktifkannya",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        TextButton(
                            onClick = dismissWithAnimation,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nanti Saja (Alarm Mungkin Tertunda)",
                                style = KanguruTheme.typography.labelMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
}
}

@Composable
private fun GuidancePill(
    text: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isHighlight) colors.legalRoseTintBg else colors.surface,
        border = BorderStroke(
            1.dp,
            if (isHighlight) colors.primary.copy(alpha = 0.5f) else colors.divider
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) colors.primary else colors.textPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun GuidanceArrow() {
    val colors = KanguruTheme.colors
    Text(
        text = "›",
        style = KanguruTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = colors.textSecondary,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

@Composable
private fun GuidanceActionPill(
    text: String,
    icon: ImageVector = Icons.Default.CheckCircle,
    color: Color = KanguruTheme.colors.legalTealAccent
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(fallback)
            } catch (_: Exception) {
            }
        }
    }
}

private fun openBatteryOptimizationSettings(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(fallback)
        } catch (_: Exception) {
        }
    }
}
