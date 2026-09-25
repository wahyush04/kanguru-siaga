package com.kangurusiaga.app.presentation.education

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.education.GetEducationModuleUseCase
import com.kangurusiaga.app.domain.usecase.education.GetEducationModulesUseCase
import com.kangurusiaga.app.domain.usecase.education.MarkEducationCompletedUseCase
import com.kangurusiaga.app.domain.usecase.education.RecordEducationLastReadUseCase
import com.kangurusiaga.app.domain.usecase.education.ToggleEducationFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEducationModuleUseCase: GetEducationModuleUseCase,
    private val getEducationModulesUseCase: GetEducationModulesUseCase,
    private val toggleEducationFavoriteUseCase: ToggleEducationFavoriteUseCase,
    private val markEducationCompletedUseCase: MarkEducationCompletedUseCase,
    private val recordEducationLastReadUseCase: RecordEducationLastReadUseCase
) : ViewModel() {

    val moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(EducationDetailUiState(isLoading = true))
    val uiState: StateFlow<EducationDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        recordLastRead()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            combine(
                getEducationModuleUseCase(moduleId),
                getEducationModulesUseCase()
            ) { currentModule, allModules ->
                val nextModule = allModules.firstOrNull { it.order == (currentModule?.order ?: 0) + 1 }
                EducationDetailUiState(
                    module = currentModule,
                    nextModuleId = nextModule?.id,
                    nextModuleOrder = nextModule?.order,
                    nextModuleTitle = nextModule?.title,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun recordLastRead() {
        viewModelScope.launch {
            recordEducationLastReadUseCase(moduleId)
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            toggleEducationFavoriteUseCase(moduleId)
        }
    }

    fun markCompleted() {
        viewModelScope.launch {
            markEducationCompletedUseCase(moduleId)
        }
    }
}
