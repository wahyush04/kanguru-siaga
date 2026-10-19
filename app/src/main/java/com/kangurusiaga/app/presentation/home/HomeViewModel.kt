package com.kangurusiaga.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.PmkTimerManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetTodayPmkSessionsUseCase
import com.kangurusiaga.app.domain.usecase.notification.GetUnreadNotificationCountUseCase
import com.kangurusiaga.app.domain.usecase.notification.SyncInAppNotificationsUseCase
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getTodayPmkSessionsUseCase: GetTodayPmkSessionsUseCase,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase? = null,
    private val syncInAppNotificationsUseCase: SyncInAppNotificationsUseCase? = null,
    private val pmkTimerManager: PmkTimerManager? = null
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
                viewModelScope.launch {
                    syncInAppNotificationsUseCase?.invoke(baby.id)
                    pmkTimerManager?.ensureTickerRunning()
                    pmkTimerManager?.refreshTodayTotal()
                }
                val timerFlow = pmkTimerManager?.timerState ?: flowOf(PmkTimerState())
                val unreadFlow = getUnreadNotificationCountUseCase?.invoke(baby.id) ?: flowOf(0)

                combine(
                    getTodayPmkSessionsUseCase(baby.id),
                    unreadFlow,
                    timerFlow,
                    _dialogState
                ) { sessions, unreadCount, timerState, dialogs ->
                    buildHomeState(baby, sessions, unreadCount, timerState, dialogs)
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
        unreadCount: Int,
        timerState: PmkTimerState,
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
        val timerTotalMinutes = (timerState.todayTotalActiveSeconds / 60).toInt()
        val dbTotalMinutes = sessions.sumOf { it.activeDurationMinutes.coerceAtLeast(it.durationMinutes) }
        val totalMinutes = maxOf(timerTotalMinutes, dbTotalMinutes)

        val hours = totalMinutes / 60
        val minutesRemainder = totalMinutes % 60
        val targetHours = timerState.targetDailyHours
        val progress = (totalMinutes.toFloat() / (targetHours * 60f)).coerceIn(0f, 1f)

        val progressText = when {
            hours > 0 && minutesRemainder > 0 -> "${hours} Jam ${minutesRemainder} Menit"
            hours > 0 -> "${hours} Jam"
            minutesRemainder > 0 -> "${minutesRemainder} Menit"
            else -> "0 Menit"
        }

        return HomeUiState(
            isLoading = false,
            activeBaby = baby,
            babyAgeFormatted = ageText,
            babyWeightFormatted = weightText,
            todayPmkSessions = sessions,
            todaySessionsCount = count,
            todayTargetSessions = targetHours,
            todayPmkMinutesTotal = totalMinutes,
            todayTargetHours = targetHours,
            formattedTodayPmkProgress = progressText,
            todayProgressFraction = progress,
            unreadNotificationsCount = unreadCount,
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
