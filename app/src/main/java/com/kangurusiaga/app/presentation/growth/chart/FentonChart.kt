package com.kangurusiaga.app.presentation.growth.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kangurusiaga.app.R
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.data.local.fenton.FentonCurvePoint
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import kotlin.math.ceil

@Composable
fun FentonChart(
    parameter: GrowthParameter,
    isBoy: Boolean,
    measurements: List<GrowthMeasurement>,
    babyName: String = "si kecil",
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val gender = if (isBoy) Gender.MALE else Gender.FEMALE
    val curvePoints: List<FentonCurvePoint> = remember(parameter, isBoy) {
        FentonReferenceData.getCurvePoints(parameter, gender)
    }

    // Determine Y range and ticks dynamically so all percentiles and measurements are fully visible.
    // Specifically:
    // - Weight: Fenton 2013 P97 reaches 7.275 kg at week 50. Default maxY = 8f fits all percentiles up to 50 weeks!
    // - Length: Fenton 2013 P97 reaches ~60.3 cm. Default maxY = 70f fits all percentiles!
    // - Head Circumference: Fenton 2013 P97 reaches ~41 cm. Default maxY = 45f fits all percentiles!
    val (minY, maxY, yTicks) = remember(parameter, measurements) {
        when (parameter) {
            GrowthParameter.WEIGHT -> {
                val maxMeas = measurements.maxOfOrNull { it.value } ?: 0f
                val topY = if (maxMeas > 7.5f) ceil(maxMeas + 0.5f) else 8f
                val ticks = (0..topY.toInt()).map { it.toFloat() }
                Triple(0f, topY, ticks)
            }
            GrowthParameter.LENGTH -> {
                val maxMeas = measurements.maxOfOrNull { it.value } ?: 0f
                val topY = if (maxMeas > 68f) ceil((maxMeas + 5f) / 10f) * 10f else 70f
                val ticks = generateSequence(20f) { it + 10f }.takeWhile { it <= topY }.toList()
                Triple(20f, topY, ticks)
            }
            GrowthParameter.HEAD_CIRCUMFERENCE -> {
                val maxMeas = measurements.maxOfOrNull { it.value } ?: 0f
                val topY = if (maxMeas > 43f) ceil((maxMeas + 5f) / 5f) * 5f else 45f
                val ticks = generateSequence(15f) { it + 5f }.takeWhile { it <= topY }.toList()
                Triple(15f, topY, ticks)
            }
        }
    }

    val minX = 22f
    val maxX = 50f
    val xTicks = listOf(22f, 26f, 30f, 34f, 38f, 42f, 46f, 50f)

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Chart Card Header: Title + Gender Pill Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandPink)
                )
                Text(
                    text = stringResource(R.string.growth_chart_fenton_curve_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            }

            // Gender Pill Badge
            val genderBg = if (isBoy) Color(0xFFEFF6FF) else Color(0xFFFFF0F3)
            val genderBorder = if (isBoy) Color(0xFFDBEAFE) else Color(0xFFFFE2E6)
            val genderColor = if (isBoy) Color(0xFF2563EB) else BrandPink
            val genderSymbol = if (isBoy) "♂" else "♀"
            val genderLabel = if (isBoy) stringResource(R.string.baby_profile_gender_male) else stringResource(R.string.baby_profile_gender_female)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(genderBg)
                    .border(1.dp, genderBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = genderSymbol,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = genderColor
                )
                Text(
                    text = genderLabel,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = genderColor
                )
            }
        }

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Y-axis unit label
        Text(
            text = when (parameter) {
                GrowthParameter.WEIGHT -> stringResource(R.string.growth_chart_unit_weight)
                GrowthParameter.LENGTH -> stringResource(R.string.growth_chart_unit_length)
                GrowthParameter.HEAD_CIRCUMFERENCE -> stringResource(R.string.growth_chart_unit_head)
            },
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
        )

        // 3. Chart Canvas Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val leftPad = 26.dp.toPx()
                val rightPad = 38.dp.toPx()
                val topPad = 12.dp.toPx()
                val bottomPad = 22.dp.toPx()

                val plotLeft = leftPad
                val plotRight = size.width - rightPad
                val plotTop = topPad
                val plotBottom = size.height - bottomPad

                val plotWidth = plotRight - plotLeft
                val plotHeight = plotBottom - plotTop

                fun pmaToX(pma: Float): Float {
                    val clamped = pma.coerceIn(minX, maxX)
                    return plotLeft + ((clamped - minX) / (maxX - minX)) * plotWidth
                }

                fun valToY(v: Float): Float {
                    val clamped = v.coerceIn(minY, maxY)
                    return plotBottom - ((clamped - minY) / (maxY - minY)) * plotHeight
                }

                val gridColor = Color(0xFFF1F5F9)
                val baseLineColor = Color(0xFFE2E8F0)
                val axisTextColor = Color(0xFF9CA3AF)
                val labelStyle = TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = axisTextColor,
                    textAlign = TextAlign.End
                )

                // 1. Draw horizontal grid lines & Y ticks
                yTicks.forEach { tick ->
                    val y = valToY(tick)
                    val isBase = (tick == minY)
                    drawLine(
                        color = if (isBase) baseLineColor else gridColor,
                        start = Offset(plotLeft, y),
                        end = Offset(plotRight, y),
                        strokeWidth = if (isBase) 1.2.dp.toPx() else 0.8.dp.toPx()
                    )

                    // Y label
                    val tickText = if (tick % 1f == 0f) tick.toInt().toString() else tick.toString()
                    val textLayout = textMeasurer.measure(
                        text = tickText,
                        style = labelStyle
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(plotLeft - textLayout.size.width - 6.dp.toPx(), y - (textLayout.size.height / 2f))
                    )
                }

                // 2. Draw vertical grid lines & X ticks
                xTicks.forEach { tick ->
                    val x = pmaToX(tick)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, plotTop),
                        end = Offset(x, plotBottom),
                        strokeWidth = 0.8.dp.toPx()
                    )

                    // X tick text below bottom line
                    val tickText = tick.toInt().toString()
                    val textLayout = textMeasurer.measure(
                        text = tickText,
                        style = labelStyle.copy(textAlign = TextAlign.Center)
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(x - (textLayout.size.width / 2f), plotBottom + 4.dp.toPx())
                    )
                }

                // 3. Draw Fenton Curves (P97, P90, P50, P10, P3)
                fun drawFentonCurve(
                    valueSelector: (FentonCurvePoint) -> Float,
                    curveColor: Color,
                    strokeWidth: Float,
                    percentileLabel: String
                ) {
                    if (curvePoints.isEmpty()) return
                    val path = Path()
                    curvePoints.forEachIndexed { idx, pt ->
                        val x = pmaToX(pt.pmaWeeks)
                        val y = valToY(valueSelector(pt))
                        if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = curveColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw curve label at the right end
                    val lastPt = curvePoints.last()
                    val endX = pmaToX(lastPt.pmaWeeks) + 4.dp.toPx()
                    val endY = valToY(valueSelector(lastPt)) - 5.dp.toPx()
                    val tagLayout = textMeasurer.measure(
                        text = "- $percentileLabel",
                        style = TextStyle(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = curveColor
                        )
                    )
                    drawText(
                        textLayoutResult = tagLayout,
                        topLeft = Offset(endX, endY)
                    )
                }

                // Draw curves from outer to inner
                drawFentonCurve({ it.p97 }, Color(0xFFEF4444), 1.2.dp.toPx(), "P97")
                drawFentonCurve({ it.p90 }, Color(0xFFF97316), 1.2.dp.toPx(), "P90")
                drawFentonCurve({ it.p50 }, Color(0xFF10B981), 1.8.dp.toPx(), "P50")
                drawFentonCurve({ it.p10 }, Color(0xFFF59E0B), 1.2.dp.toPx(), "P10")
                drawFentonCurve({ it.p3 }, Color(0xFFFB7185), 1.2.dp.toPx(), "P3")

                // 4. Draw Baby's trajectory if any data points exist
                val validMeasurements = measurements.filter { it.pmaWeeks in minX..maxX }
                if (validMeasurements.isNotEmpty()) {
                    val babyPath = Path()
                    validMeasurements.forEachIndexed { index, m ->
                        val x = pmaToX(m.pmaWeeks)
                        val y = valToY(m.value)
                        if (index == 0) babyPath.moveTo(x, y) else babyPath.lineTo(x, y)
                    }
                    drawPath(
                        path = babyPath,
                        color = BrandPink,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw historical points & latest point
                    validMeasurements.forEachIndexed { index, m ->
                        val x = pmaToX(m.pmaWeeks)
                        val y = valToY(m.value)
                        val isLatest = (index == validMeasurements.lastIndex)

                        if (isLatest) {
                            // Latest point: larger filled circle with white border
                            drawCircle(
                                color = BrandPink,
                                radius = 4.5.dp.toPx(),
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = White,
                                radius = 4.5.dp.toPx(),
                                center = Offset(x, y),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        } else {
                            // Historical point: white with BrandPink border
                            drawCircle(
                                color = White,
                                radius = 3.2.dp.toPx(),
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = BrandPink,
                                radius = 3.2.dp.toPx(),
                                center = Offset(x, y),
                                style = Stroke(width = 1.8.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 4. X-axis label
        Text(
            text = stringResource(R.string.growth_chart_x_axis_label),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // 5. Detailed Legend Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.growth_chart_legend_title),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
            )
            Text(
                text = "Fenton Preterm Growth",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF9CA3AF)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Baby Trajectory Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF1F3))
                .border(1.dp, Color(0xFFFFE4E6), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // — • — symbol
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(BrandPink)
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(BrandPink)
                        .border(1.dp, White, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(BrandPink)
                )
            }

            Text(
                text = stringResource(R.string.growth_chart_legend_plot, parameter.displayName, babyName),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandPink
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2-Column Grid for percentiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Column 1: P50 & P10
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // P50
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 2.5.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF047857))) {
                                append("P50")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))) {
                                append(" (Median / Normal)")
                            }
                        },
                        fontSize = 10.sp
                    )
                }

                // P10
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 2.5.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFFF59E0B))
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFD97706))) {
                                append("P10")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))) {
                                append(" (Batas Bawah)")
                            }
                        },
                        fontSize = 10.sp
                    )
                }
            }

            // Column 2: P90 & P97/P3
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // P90
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 2.5.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFFF97316))
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))) {
                                append("P90")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))) {
                                append(" (Batas Atas)")
                            }
                        },
                        fontSize = 10.sp
                    )
                }

                // P97 & P3
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = 2.5.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(Color(0xFFEF4444))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = 2.5.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(Color(0xFFFB7185))
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))) {
                                append("P97")
                            }
                            withStyle(SpanStyle(color = Color(0xFF6B7280))) {
                                append(" & ")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E))) {
                                append("P3")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Color(0xFF6B7280))) {
                                append(" (Ekstrem)")
                            }
                        },
                        fontSize = 9.5.sp
                    )
                }
            }
        }
    }
}
