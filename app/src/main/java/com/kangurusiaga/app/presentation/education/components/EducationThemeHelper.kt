package com.kangurusiaga.app.presentation.education.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kangurusiaga.app.core.designsystem.theme.DarkKanguruColors
import com.kangurusiaga.app.core.designsystem.theme.KanguruColors
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.LightKanguruColors

data class EducationColorPalette(
    val primary: Color,
    val background: Color,
    val border: Color
)

object EducationThemeHelper {
    @Composable
    fun getPalette(colorToken: String): EducationColorPalette {
        return getPalette(colorToken, KanguruTheme.colors)
    }

    fun getPalette(colorToken: String, colors: KanguruColors): EducationColorPalette {
        return when (colorToken.lowercase()) {
            "rose" -> EducationColorPalette(colors.categoryRose, colors.categoryRoseContainer, colors.categoryRoseBorder)
            "amber", "yellow" -> EducationColorPalette(colors.warning, colors.warningContainer, colors.warningBorder)
            "sky" -> EducationColorPalette(colors.info, colors.infoContainer, colors.infoBorder)
            "orange" -> EducationColorPalette(colors.categoryOrange, colors.categoryOrangeContainer, colors.categoryOrangeBorder)
            "purple" -> EducationColorPalette(colors.categoryPurple, colors.categoryPurpleContainer, colors.categoryPurpleBorder)
            "pink" -> EducationColorPalette(colors.primary, colors.primaryContainer, colors.primaryBorder)
            "teal" -> EducationColorPalette(colors.categoryTeal, colors.categoryTealContainer, colors.categoryTealBorder)
            "emerald" -> EducationColorPalette(colors.success, colors.successContainer, colors.successBorder)
            else -> EducationColorPalette(colors.categoryRose, colors.categoryRoseContainer, colors.categoryRoseBorder)
        }
    }

    fun getPalette(colorToken: String, isDark: Boolean = false): EducationColorPalette {
        val colors = if (isDark) DarkKanguruColors else LightKanguruColors
        return getPalette(colorToken, colors)
    }
}
