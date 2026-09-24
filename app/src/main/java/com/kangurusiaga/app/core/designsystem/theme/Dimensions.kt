package com.kangurusiaga.app.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val spaceNone: Dp = 0.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceExtraLarge: Dp = 32.dp,
    val spaceDoubleExtraLarge: Dp = 48.dp,

    val cardElevation: Dp = 2.dp,
    val cardRadius: Dp = 16.dp,
    val buttonHeight: Dp = 52.dp,
    val iconSizeSmall: Dp = 18.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }
