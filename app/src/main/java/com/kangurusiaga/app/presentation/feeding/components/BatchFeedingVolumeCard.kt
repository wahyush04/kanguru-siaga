package com.kangurusiaga.app.presentation.feeding.components

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

@Composable
fun BatchFeedingVolumeCard(
    activeVolumeMl: Int,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = KanguruTheme.colors.primaryBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top section: Icon + Title + Badge + Subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tune/Sliders Icon in rounded container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KanguruTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = KanguruTheme.colors.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.alarm_batch_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary
                    )

                    // Badge "SEMUA JADWAL"
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(KanguruTheme.colors.primaryContainer)
                            .border(
                                width = 1.dp,
                                color = KanguruTheme.colors.primaryBorder,
                                shape = CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.alarm_batch_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.alarm_batch_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = KanguruTheme.colors.textSecondary
                )
            }
        }

        // Divider
        HorizontalDivider(
            thickness = 1.dp,
            color = KanguruTheme.colors.divider
        )

        // Bottom row: "Dosis Aktif: [30 ml]" and "Atur Dosis ›"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.alarm_batch_active_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = KanguruTheme.colors.textSecondary
                )

                // Active Volume Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = KanguruTheme.colors.outlineVariant,
                            shape = CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.alarm_volume_unit, activeVolumeMl),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary
                    )
                }
            }

            // "Atur Dosis ›" Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(KanguruTheme.colors.primaryContainer)
                    .clickable { onManageClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.alarm_batch_btn_manage),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = KanguruTheme.colors.onPrimaryContainer
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = KanguruTheme.colors.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
