package com.app.quizapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.security.TokenManager
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for SettingsScreen
 * @param isAdmin Whether user has admin role
 * @param isLoading Whether data is being loaded
 * @param shouldNavigateToWelcome Trigger for logout navigation
 */
data class SettingsUiState(
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val shouldNavigateToWelcome: Boolean = false
)

/**
 * ViewModel for Settings screen
 * Handles logout and admin status determination
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    /**
     * Load current user information to determine admin status
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    val isAdmin = result.data.userRole.userRole== "ROLE_ADMIN"
                    _uiState.update {
                        it.copy(
                            isAdmin = isAdmin,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    // On error, assume not admin and continue
                    _uiState.update {
                        it.copy(
                            isAdmin = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun logoutUser(){
        viewModelScope.launch {
            logout()
        }
    }

    /**
     * Perform logout operation
     * Clears token and triggers navigation to Welcome screen
     */
    suspend fun logout() {
        tokenManager.clearToken()
        _uiState.update { it.copy(shouldNavigateToWelcome = true) }
    }

    /**
     * Reset navigation state after navigation is handled
     */
    fun onNavigationHandled() {
        _uiState.update { it.copy(shouldNavigateToWelcome = false) }
    }
}
