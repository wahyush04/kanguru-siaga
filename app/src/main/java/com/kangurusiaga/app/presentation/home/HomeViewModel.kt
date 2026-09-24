package com.kangurusiaga.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getBabyProfileUseCase: GetBabyProfileUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getBabyProfileUseCase()
        .map { baby ->
            HomeUiState(
                isLoading = false,
                activeBaby = baby
            )
        }
        .catch { throwable ->
            emit(
                HomeUiState(
                    isLoading = false,
                    userMessage = throwable.localizedMessage ?: "Terjadi kesalahan"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(isLoading = true)
        )
}
