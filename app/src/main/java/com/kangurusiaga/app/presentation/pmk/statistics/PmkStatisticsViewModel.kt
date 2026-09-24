package com.kangurusiaga.app.presentation.pmk.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.domain.usecase.DeletePmkSessionUseCase
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetPmkSessionsUseCase
import com.kangurusiaga.app.domain.usecase.GetPmkStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

data class PmkStatisticsUiState(
    val isLoading: Boolean = true,
    val baby: Baby? = null,
    val selectedPeriod: StatsPeriod = StatsPeriod.THIS_WEEK,
    val statistics: PmkStatistics = PmkStatistics(),
    val sessions: List<PmkSession> = emptyList(),
    val message: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PmkStatisticsViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getPmkStatisticsUseCase: GetPmkStatisticsUseCase,
    private val getPmkSessionsUseCase: GetPmkSessionsUseCase,
    private val deletePmkSessionUseCase: DeletePmkSessionUseCase
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.THIS_WEEK)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PmkStatisticsUiState> = getBabyProfileUseCase()
        .flatMapLatest { baby ->
            val babyId = baby?.id ?: 1L
            combine(
                _selectedPeriod.flatMapLatest { period ->
                    getPmkStatisticsUseCase(babyId, period)
                },
                getPmkSessionsUseCase(babyId),
                _selectedPeriod,
                _userMessage
            ) { stats, sessions, period, message ->
                PmkStatisticsUiState(
                    isLoading = false,
                    baby = baby,
                    selectedPeriod = period,
                    statistics = stats,
                    sessions = sessions,
                    message = message
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PmkStatisticsUiState()
        )

    fun onPeriodSelected(period: StatsPeriod) {
        _selectedPeriod.value = period
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            deletePmkSessionUseCase(sessionId)
            _userMessage.value = "Catatan sesi berhasil dihapus"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
