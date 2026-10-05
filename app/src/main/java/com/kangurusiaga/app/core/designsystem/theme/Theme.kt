package com.kangurusiaga.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode

private val LightColorScheme = lightColorScheme(
    primary = BrandPink,
    onPrimary = White,
    primaryContainer = BrandLightPink,
    onPrimaryContainer = BrandDarkPink,
    secondary = BrandPeach,
    onSecondary = TextPrimary,
    secondaryContainer = BrandPeach,
    onSecondaryContainer = TextPrimary,
    tertiary = BrandTextGreen,
    onTertiary = White,
    tertiaryContainer = BrandSoftGreen,
    onTertiaryContainer = BrandTextGreen,
    background = BrandBackground,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = BrandCardBorder,
    onSurfaceVariant = TextSecondary,
    outline = BrandCardBorder,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandPink,
    onPrimary = White,
    primaryContainer = BrandDarkPink,
    onPrimaryContainer = BrandLightPink,
    secondary = BrandPeach,
    onSecondary = TextPrimary,
    background = Color(0xFF141312),
    onBackground = White,
    surface = Color(0xFF1E1D1B),
    onSurface = White,
    surfaceVariant = Color(0xFF2E2C29),
    onSurfaceVariant = Color(0xFFC7C5C2),
    outline = Color(0xFF3F3D3A)
)

object KanguruTheme {
    val dimensions: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}

@Composable
fun KanguruSiagaTheme(
    themeMode: AppThemeMode = AppThemeMode.WARM_LIGHT,
    textScale: AppTextScale = AppTextScale.STANDARD,
    darkTheme: Boolean = when (themeMode) {
        AppThemeMode.WARM_LIGHT -> false
        AppThemeMode.DIM_NURSING -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val dimensions = Dimensions()
    val scaledTypography = getScaledTypography(textScale.scaleFactor)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDimensions provides dimensions,
        LocalTextScaleFactor provides textScale.scaleFactor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            shapes = KanguruShapes,
            content = content
        )
    }
}
