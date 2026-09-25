package com.kangurusiaga.app.presentation.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.emergency.GetEmergencyWarningModulesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyWarningListViewModel @Inject constructor(
    private val getEmergencyWarningModulesUseCase: GetEmergencyWarningModulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyWarningListUiState())
    val uiState: StateFlow<EmergencyWarningListUiState> = _uiState.asStateFlow()

    init {
        loadModules()
    }

    fun loadModules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEmergencyWarningModulesUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Gagal memuat tanda kegawatan"
                        )
                    }
                }
                .collect { modules ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            modules = modules,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
