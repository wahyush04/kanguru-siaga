package com.kangurusiaga.app.presentation.education

import com.kangurusiaga.app.domain.model.education.EducationModule

enum class EducationTab {
    DAFTAR_MATERI,
    MATERI_FAVORIT
}

data class EducationListUiState(
    val selectedTab: EducationTab = EducationTab.DAFTAR_MATERI,
    val allModules: List<EducationModule> = emptyList(),
    val favoriteModules: List<EducationModule> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
) {
    val displayedModules: List<EducationModule>
        get() = when (selectedTab) {
            EducationTab.DAFTAR_MATERI -> allModules
            EducationTab.MATERI_FAVORIT -> favoriteModules
        }
}
