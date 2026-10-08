package com.kangurusiaga.app.presentation.pmk.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.domain.usecase.TimelineBlock

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PmkTimeline24HourBar(
    timeline: DailyTimeline?,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Timeline Ritme 24 Jam",
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Kontak PMK vs Jeda Perawatan Hari Ini",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
            if (timeline != null) {
                val actHours = timeline.totalActiveMinutes / 60
                val actMins = timeline.totalActiveMinutes % 60
                Text(
                    text = "${actHours}j ${actMins}m / 24j",
                    style = KanguruTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual 24-Hour Bar
        val blocks = timeline?.blocks ?: emptyList()
        val trackColor = colors.surfaceVariant
        val activeColor = colors.primary
        val pauseColor = colors.fentonP10

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(trackColor)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                .padding(2.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val totalWidth = size.width
                val barHeight = size.height

                // Draw segments
                for (block in blocks) {
                    val startX = block.startFraction * totalWidth
                    val blockWidth = (block.endFraction - block.startFraction) * totalWidth
                    val color = if (block.isPaused) pauseColor else activeColor

                    if (blockWidth > 0f) {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(startX, 0f),
                            size = Size(blockWidth, barHeight),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    }
                }

                // Draw hour tick indicators (every 6 hours)
                val tickFractions = listOf(0.25f, 0.5f, 0.75f)
                for (fraction in tickFractions) {
                    val x = fraction * totalWidth
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = Offset(x, 0f),
                        end = Offset(x, barHeight),
                        strokeWidth = 1.5f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hour labels: 00:00, 06:00, 12:00, 18:00, 24:00
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("00:00", "06:00", "12:00", "18:00", "24:00").forEach { label ->
                Text(
                    text = label,
                    fontFamily = FontFamily.Monospace,
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
        }

        if (showLegend && timeline != null) {
            val actH = timeline.totalActiveMinutes / 60
            val actM = timeline.totalActiveMinutes % 60
            val pauseH = timeline.totalPauseMinutes / 60
            val pauseM = timeline.totalPauseMinutes % 60

            val actStr = if (actH > 0) "${actH}j ${actM}m" else "${actM}m"
            val pauseStr = if (pauseH > 0) "${pauseH}j ${pauseM}m" else "${pauseM}m"

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = colors.divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Stitch-exact Legend: PMK Aktif, Jeda Singkat, Tersisa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. PMK Aktif
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(activeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PMK Aktif ($actStr)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }

                // 2. Jeda Singkat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(pauseColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jeda Singkat ($pauseStr)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }

                // 3. Tersisa
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.outlineVariant, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tersisa",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}
