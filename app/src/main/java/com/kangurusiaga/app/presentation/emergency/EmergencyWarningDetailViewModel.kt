package com.kangurusiaga.app.presentation.emergency

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.emergency.GetEmergencyWarningModuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyWarningDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEmergencyWarningModuleUseCase: GetEmergencyWarningModuleUseCase
) : ViewModel() {

    val moduleId: String = savedStateHandle["moduleId"] ?: "emergency_01"

    private val _uiState = MutableStateFlow(EmergencyWarningDetailUiState(isLoading = true))
    val uiState: StateFlow<EmergencyWarningDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail(moduleId)
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEmergencyWarningModuleUseCase(id)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Gagal memuat detail modul"
                        )
                    }
                }
                .collect { module ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            module = module,
                            errorMessage = if (module == null) "Modul tidak ditemukan" else null
                        )
                    }
                }
        }
    }
}
