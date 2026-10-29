package com.kangurusiaga.app.presentation.pmk.recap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.WeeklyRecapData
import com.kangurusiaga.app.domain.model.WeeklyRecapTab
import com.kangurusiaga.app.domain.usecase.GetPmkWeeklyRecapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PmkWeeklyRecapUiState(
    val isLoading: Boolean = true,
    val selectedTab: WeeklyRecapTab = WeeklyRecapTab.REKAP_MINGGUAN,
    val data: WeeklyRecapData = WeeklyRecapData()
)

@HiltViewModel
class PmkWeeklyRecapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPmkWeeklyRecapUseCase: GetPmkWeeklyRecapUseCase
) : ViewModel() {

    private val initialTabOrdinal = savedStateHandle.get<String>("tab")?.toIntOrNull() ?: 0
    private val initialTab = if (initialTabOrdinal == 1) WeeklyRecapTab.KORELASI_PERTUMBUHAN else WeeklyRecapTab.REKAP_MINGGUAN

    private val _selectedTab = MutableStateFlow(initialTab)

    val uiState: StateFlow<PmkWeeklyRecapUiState> = combine(
        _selectedTab,
        getPmkWeeklyRecapUseCase()
    ) { tab, data ->
        PmkWeeklyRecapUiState(
            isLoading = false,
            selectedTab = tab,
            data = data
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PmkWeeklyRecapUiState(selectedTab = initialTab)
    )

    fun onTabSelected(tab: WeeklyRecapTab) {
        _selectedTab.value = tab
    }
}
