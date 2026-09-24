package com.kangurusiaga.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.GetOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object ShowSplash : SplashUiState
    data object NavigateToHome : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    getOnboardingStatusUseCase: GetOnboardingStatusUseCase
) : ViewModel() {

    val uiState: StateFlow<SplashUiState> = getOnboardingStatusUseCase()
        .map { isCompleted ->
            if (isCompleted) {
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.ShowSplash
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SplashUiState.Loading
        )
}
