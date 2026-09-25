package com.kangurusiaga.app.domain.model.emergency

data class EmergencyWarningModule(
    val id: String,
    val order: Int,
    val title: String,
    val category: String,
    val iconType: String,
    val themeColor: String,
    val heroDrawable: String,
    val heroTag: String,
    val sections: List<EmergencySection>
)

sealed interface EmergencySection {
    data class SymptomsList(
        val title: String,
        val items: List<String>
    ) : EmergencySection

    data class ActionAlert(
        val title: String,
        val message: String,
        val themeColor: String = "amber"
    ) : EmergencySection
}
