package com.kangurusiaga.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetTodayPmkSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getTodayPmkSessionsUseCase: GetTodayPmkSessionsUseCase
) : ViewModel() {

    private val _dialogState = MutableStateFlow(
        DialogState(
            showEmergency = false,
            showNotification = false,
            infoMessage = null
        )
    )

    private data class DialogState(
        val showEmergency: Boolean,
        val showNotification: Boolean,
        val infoMessage: String?
    )

    val uiState: StateFlow<HomeUiState> = getBabyProfileUseCase()
        .flatMapLatest { baby ->
            if (baby != null) {
                combine(
                    getTodayPmkSessionsUseCase(baby.id),
                    _dialogState
                ) { sessions, dialogs ->
                    buildHomeState(baby, sessions, dialogs)
                }
            } else {
                _dialogState.map { dialogs ->
                    HomeUiState(
                        isLoading = false,
                        activeBaby = null,
                        showEmergencyDialog = dialogs.showEmergency,
                        showNotificationSheet = dialogs.showNotification,
                        infoMessage = dialogs.infoMessage
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(isLoading = true)
        )

    private fun buildHomeState(
        baby: Baby,
        sessions: List<PmkSession>,
        dialogs: DialogState
    ): HomeUiState {
        val now = System.currentTimeMillis()
        val diffMillis = (now - baby.birthDateEpochMillis).coerceAtLeast(0L)
        val days = diffMillis / (24 * 3600 * 1000L)
        val weeks = days / 7
        val ageText = when {
            weeks >= 1 -> "Usia $weeks minggu"
            days > 0 -> "Usia $days hari"
            else -> "Usia baru lahir"
        }

        val formattedWeight = NumberFormat.getIntegerInstance(Locale("id", "ID")).format(baby.birthWeightGram)
        val weightText = "BBLR $formattedWeight gram"

        val count = sessions.size
        val target = 3
        val progress = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)

        return HomeUiState(
            isLoading = false,
            activeBaby = baby,
            babyAgeFormatted = ageText,
            babyWeightFormatted = weightText,
            todayPmkSessions = sessions,
            todaySessionsCount = count,
            todayTargetSessions = target,
            todayProgressFraction = progress,
            unreadNotificationsCount = 1,
            showEmergencyDialog = dialogs.showEmergency,
            showNotificationSheet = dialogs.showNotification,
            infoMessage = dialogs.infoMessage
        )
    }

    fun openEmergencyDialog() {
        _dialogState.update { it.copy(showEmergency = true) }
    }

    fun closeEmergencyDialog() {
        _dialogState.update { it.copy(showEmergency = false) }
    }

    fun openNotificationSheet() {
        _dialogState.update { it.copy(showNotification = true) }
    }

    fun closeNotificationSheet() {
        _dialogState.update { it.copy(showNotification = false) }
    }

    fun showInfoMessage(message: String) {
        _dialogState.update { it.copy(infoMessage = message) }
    }

    fun clearInfoMessage() {
        _dialogState.update { it.copy(infoMessage = null) }
    }
}
