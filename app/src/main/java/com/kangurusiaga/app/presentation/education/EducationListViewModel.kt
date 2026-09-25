package com.kangurusiaga.app.presentation.education

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.domain.usecase.education.GetEducationModulesUseCase
import com.kangurusiaga.app.domain.usecase.education.GetFavoriteEducationModulesUseCase
import com.kangurusiaga.app.domain.usecase.education.ToggleEducationFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducationListViewModel @Inject constructor(
    private val getEducationModulesUseCase: GetEducationModulesUseCase,
    private val getFavoriteEducationModulesUseCase: GetFavoriteEducationModulesUseCase,
    private val toggleEducationFavoriteUseCase: ToggleEducationFavoriteUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(EducationTab.DAFTAR_MATERI)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(EducationListUiState(isLoading = true))
    val uiState: StateFlow<EducationListUiState> = _uiState.asStateFlow()

    init {
        loadModules()
    }

    private fun loadModules() {
        viewModelScope.launch {
            combine(
                _selectedTab,
                getEducationModulesUseCase(),
                getFavoriteEducationModulesUseCase(),
                _userMessage
            ) { tab, all, favorites, message ->
                EducationListUiState(
                    selectedTab = tab,
                    allModules = all,
                    favoriteModules = favorites,
                    isLoading = false,
                    userMessage = message
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(tab: EducationTab) {
        _selectedTab.value = tab
    }

    fun toggleBookmark(moduleId: String) {
        viewModelScope.launch {
            toggleEducationFavoriteUseCase(moduleId)
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
