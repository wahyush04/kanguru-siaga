package com.kangurusiaga.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.usecase.settings.GetAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getAppSettingsUseCase: GetAppSettingsUseCase
) : ViewModel() {
    val settings: StateFlow<AppSettings> = getAppSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
}
