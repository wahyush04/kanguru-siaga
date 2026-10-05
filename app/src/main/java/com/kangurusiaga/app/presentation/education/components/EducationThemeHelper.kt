package com.kangurusiaga.app.presentation.education.components

import androidx.compose.ui.graphics.Color

data class EducationColorPalette(
    val primary: Color,
    val background: Color,
    val border: Color
)

object EducationThemeHelper {
    fun getPalette(colorToken: String, isDark: Boolean = false): EducationColorPalette {
        val base = when (colorToken.lowercase()) {
            "rose" -> Triple(Color(0xFFFF5C77), Color(0xFFFFF0F2), Color(0xFFFFDCE2))
            "amber" -> Triple(Color(0xFFD97706), Color(0xFFFFFBEB), Color(0xFFFDE68A))
            "sky" -> Triple(Color(0xFF0284C7), Color(0xFFF0F9FF), Color(0xFFBAE6FD))
            "orange" -> Triple(Color(0xFFEA580C), Color(0xFFFFF7ED), Color(0xFFFED7AA))
            "purple" -> Triple(Color(0xFF9333EA), Color(0xFFFAF5FF), Color(0xFFE9D5FF))
            "pink" -> Triple(Color(0xFFEC4899), Color(0xFFFDF2F8), Color(0xFFFBCFE8))
            "teal" -> Triple(Color(0xFF0D9488), Color(0xFFF0FDFA), Color(0xFF99F6E4))
            "emerald" -> Triple(Color(0xFF059669), Color(0xFFECFDF5), Color(0xFFA7F3D0))
            "yellow" -> Triple(Color(0xFFCA8A04), Color(0xFFFEFCE8), Color(0xFFFEF08A))
            else -> Triple(Color(0xFFFF5C77), Color(0xFFFFF0F2), Color(0xFFFFDCE2))
        }

        return if (isDark) {
            EducationColorPalette(
                primary = base.first,
                background = base.first.copy(alpha = 0.16f),
                border = base.first.copy(alpha = 0.35f)
            )
        } else {
            EducationColorPalette(
                primary = base.first,
                background = base.second,
                border = base.third
            )
        }
    }
}
