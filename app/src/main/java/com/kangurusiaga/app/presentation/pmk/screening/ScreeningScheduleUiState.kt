package com.kangurusiaga.app.presentation.pmk.screening

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening

enum class ScreeningTab {
    REMINDERS,
    EVALUATION
}

data class RecordScreeningModalState(
    val isOpen: Boolean = false,
    val screening: PrematureScreening? = null,
    val realizationDateEpoch: Long = System.currentTimeMillis(),
    val hospitalName: String = "Poli Anak RS Hermina",
    val doctorName: String = "dr. Siti Nurhaliza, Sp.A(K) Neo",
    val clinicalResult: String = "",
    val markAsCompleted: Boolean = true
)

data class AddScreeningModalState(
    val isOpen: Boolean = false,
    val title: String = "",
    val categoryBadge: String = "Pemeriksaan Tambahan",
    val timingLabel: String = "",
    val targetDateEpoch: Long = System.currentTimeMillis(),
    val indication: String = "",
    val clinicalGoal: String = "",
    val clinicOrLab: String = ""
)

data class ScreeningScheduleUiState(
    val baby: Baby? = null,
    val babyAgeDisplay: String = "",
    val correctedAgeDisplay: String = "",
    val birthDateDisplay: String = "",
    val selectedTab: ScreeningTab = ScreeningTab.REMINDERS,
    val screenings: List<PrematureScreening> = emptyList(),
    val recordModalState: RecordScreeningModalState = RecordScreeningModalState(),
    val addModalState: AddScreeningModalState = AddScreeningModalState(),
    val infoDialogVisible: Boolean = false,
    val userMessage: String? = null,
    val isLoading: Boolean = true
) {
    val completedScreeningsCount: Int get() = screenings.count { it.isCompleted }
    val totalScreeningsCount: Int get() = screenings.size.coerceAtLeast(4)
    val completionPercentage: Int get() = if (totalScreeningsCount > 0) (completedScreeningsCount * 100) / totalScreeningsCount else 0
    val birthWeightDisplay: String get() = baby?.birthWeightGram?.let { String.format(java.util.Locale("id", "ID"), "%,d gram", it).replace(',', '.') } ?: "1.850 gram"
    val currentWeightDisplay: String get() = "2.350 gram"
    val weightGainDisplay: String get() = "(+500g)"
}
