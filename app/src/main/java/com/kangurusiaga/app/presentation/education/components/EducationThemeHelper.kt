package com.kangurusiaga.app.presentation.education.components

import androidx.compose.ui.graphics.Color

data class EducationColorPalette(
    val primary: Color,
    val background: Color,
    val border: Color
)

object EducationThemeHelper {
    fun getPalette(colorToken: String): EducationColorPalette {
        return when (colorToken.lowercase()) {
            "rose" -> EducationColorPalette(
                primary = Color(0xFFFF5C77),
                background = Color(0xFFFFF0F2),
                border = Color(0xFFFFDCE2)
            )
            "amber" -> EducationColorPalette(
                primary = Color(0xFFD97706),
                background = Color(0xFFFFFBEB),
                border = Color(0xFFFDE68A)
            )
            "sky" -> EducationColorPalette(
                primary = Color(0xFF0284C7),
                background = Color(0xFFF0F9FF),
                border = Color(0xFFBAE6FD)
            )
            "orange" -> EducationColorPalette(
                primary = Color(0xFFEA580C),
                background = Color(0xFFFFF7ED),
                border = Color(0xFFFED7AA)
            )
            "purple" -> EducationColorPalette(
                primary = Color(0xFF9333EA),
                background = Color(0xFFFAF5FF),
                border = Color(0xFFE9D5FF)
            )
            "pink" -> EducationColorPalette(
                primary = Color(0xFFEC4899),
                background = Color(0xFFFDF2F8),
                border = Color(0xFFFBCFE8)
            )
            "teal" -> EducationColorPalette(
                primary = Color(0xFF0D9488),
                background = Color(0xFFF0FDFA),
                border = Color(0xFF99F6E4)
            )
            "emerald" -> EducationColorPalette(
                primary = Color(0xFF059669),
                background = Color(0xFFECFDF5),
                border = Color(0xFFA7F3D0)
            )
            "yellow" -> EducationColorPalette(
                primary = Color(0xFFCA8A04),
                background = Color(0xFFFEFCE8),
                border = Color(0xFFFEF08A)
            )
            else -> EducationColorPalette(
                primary = Color(0xFFFF5C77),
                background = Color(0xFFFFF0F2),
                border = Color(0xFFFFDCE2)
            )
        }
    }
}
