package com.app.quizapp.presentation.cover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.security.TokenManager
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for CoverScreen
 * @param isLoading Whether auto-login check is in progress
 * @param navigationDestination Destination to navigate to after timeout ("home" or "welcome")
 */
data class CoverUiState(
    val isLoading: Boolean = true,
    val navigationDestination: String? = null
)

/**
 * ViewModel for Cover/Splash screen with auto-login functionality
 * Checks for existing auth token and validates it with backend
 */
@HiltViewModel
class CoverViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoverUiState())
    val uiState: StateFlow<CoverUiState> = _uiState.asStateFlow()

    init {
        checkAutoLogin()
    }

    /**
     * Check if user has valid token and auto-login
     * - If token exists and valid → navigate to Home
     * - If token invalid or doesn't exist → navigate to Welcome
     */
    private fun checkAutoLogin() {
        viewModelScope.launch {
            // Show splash screen for minimum 2 seconds
            delay(2000)

            if (tokenManager.hasToken()) {
                // Token exists, validate with backend
                when (val result = userRepository.getCurrentUser()) {
                    is Result.Success -> {
                        // Token is valid, navigate to home
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                navigationDestination = "home"
                            )
                        }
                    }
                    is Result.Error -> {
                        // Token is invalid (401) or other error, clear and go to welcome
                        tokenManager.clearToken()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                navigationDestination = "welcome"
                            )
                        }
                    }
                }
            } else {
                // No token, navigate to welcome
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationDestination = "welcome"
                    )
                }
            }
        }
    }

    /**
     * Mark navigation as consumed to prevent duplicate navigation
     */
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigationDestination = null) }
    }
}
