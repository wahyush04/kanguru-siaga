package com.kangurusiaga.app.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.notification.GetNotificationsUseCase
import com.kangurusiaga.app.domain.usecase.notification.GetUnreadNotificationCountUseCase
import com.kangurusiaga.app.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.kangurusiaga.app.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.kangurusiaga.app.domain.usecase.notification.SyncInAppNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val syncInAppNotificationsUseCase: SyncInAppNotificationsUseCase
) : ViewModel() {

    private val _showEmergencySheet = MutableStateFlow(false)

    val uiState: StateFlow<NotificationUiState> = getBabyProfileUseCase()
        .flatMapLatest { baby ->
            if (baby != null) {
                viewModelScope.launch {
                    syncInAppNotificationsUseCase(baby.id)
                }
                combine(
                    getNotificationsUseCase(baby.id),
                    getUnreadNotificationCountUseCase(baby.id),
                    _showEmergencySheet
                ) { notifications, unreadCount, showEmergency ->
                    val today = notifications.filter { it.isToday }
                    val earlier = notifications.filter { !it.isToday }
                    NotificationUiState(
                        babyId = baby.id,
                        babyName = baby.name,
                        todayNotifications = today,
                        earlierNotifications = earlier,
                        unreadCount = unreadCount,
                        isLoading = false,
                        showEmergencySheet = showEmergency
                    )
                }
            } else {
                flowOf(NotificationUiState(isLoading = false))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationUiState(isLoading = true)
        )

    fun markAllAsRead() {
        val babyId = uiState.value.babyId ?: return
        viewModelScope.launch {
            markAllNotificationsAsReadUseCase(babyId)
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(notificationId)
        }
    }

    fun openEmergencySheet() {
        _showEmergencySheet.update { true }
    }

    fun closeEmergencySheet() {
        _showEmergencySheet.update { false }
    }

    fun refreshNotifications() {
        val babyId = uiState.value.babyId ?: return
        viewModelScope.launch {
            syncInAppNotificationsUseCase(babyId)
        }
    }
}
