package com.app.quizapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.repository.AuthRepository
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
 * UI state for LoginScreen
 * @param email User email input
 * @param password User password input
 * @param isLoading Whether login operation is in progress
 * @param error Error message if login fails
 * @param isLoggedIn Whether user successfully logged in
 * @param isAdmin Whether logged in user has admin role
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)

/**
 * ViewModel for Login screen
 * Handles user authentication and role determination
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Update email input
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    /**
     * Update password input
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    /**
     * Perform login operation
     * 1. Call authRepository.login()
     * 2. Save token via TokenManager
     * 3. Get current user to determine role
     * 4. Set isAdmin flag and navigate
     */
    fun login() {
        val currentState = _uiState.value

        // Validate input
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Email is required") }
            return
        }
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(error = "Password is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val loginResult = authRepository.login(currentState.email, currentState.password)) {
                is Result.Success -> {
                    // Save JWT token
                    tokenManager.saveToken(loginResult.data.token)

                    // Get current user to determine role
                    when (val userResult = userRepository.getCurrentUser()) {
                        is Result.Success -> {
                            val isAdmin = userResult.data.userRole.userRole == "ROLE_ADMIN"
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    isAdmin = isAdmin,
                                    error = null
                                )
                            }
                        }
                        is Result.Error -> {
                            // Failed to get user info, clear token
                            tokenManager.clearToken()
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to retrieve user information: ${userResult.message}"
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = loginResult.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Reset login state after successful navigation
     */
    fun resetLoginState() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }
}
