package com.app.quizapp.presentation.register

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
 * UI state for CreateAccountScreen
 * @param email User email
 * @param password User password
 * @param firstname User first name
 * @param surname User surname
 * @param birthdate User birthdate (format: dd.MM.yyyy or yyyy-MM-dd)
 * @param nickname Optional nickname
 * @param isLoading Whether registration is in progress
 * @param error Error message if registration fails
 * @param isRegistered Whether user successfully registered
 * @param isAdmin Whether registered user has admin role
 */
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val firstname: String = "",
    val surname: String = "",
    val birthdate: String = "",
    val nickname: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false,
    val isAdmin: Boolean = false
)

/**
 * ViewModel for Create Account screen
 * Handles user registration and auto-login
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

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
     * Update firstname input
     */
    fun onFirstnameChange(firstname: String) {
        _uiState.update { it.copy(firstname = firstname, error = null) }
    }

    /**
     * Update surname input
     */
    fun onSurnameChange(surname: String) {
        _uiState.update { it.copy(surname = surname, error = null) }
    }

    /**
     * Update birthdate input
     */
    fun onBirthdateChange(birthdate: String) {
        _uiState.update { it.copy(birthdate = birthdate, error = null) }
    }

    /**
     * Update nickname input
     */
    fun onNicknameChange(nickname: String) {
        _uiState.update { it.copy(nickname = nickname, error = null) }
    }

    /**
     * Perform registration operation
     * 1. Call authRepository.register()
     * 2. Save token via TokenManager (auto-login)
     * 3. Get current user to determine role
     * 4. Set isAdmin flag and navigate
     */
    fun register() {
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
        if (currentState.password.length < 8) {
            _uiState.update { it.copy(error = "Password must be at least 8 characters") }
            return
        }
        if (currentState.firstname.isBlank()) {
            _uiState.update { it.copy(error = "First name is required") }
            return
        }
        if (currentState.surname.isBlank()) {
            _uiState.update { it.copy(error = "Surname is required") }
            return
        }
        if (currentState.birthdate.isBlank()) {
            _uiState.update { it.copy(error = "Birthdate is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val registerResult = authRepository.register(
                email = currentState.email,
                password = currentState.password,
                firstname = currentState.firstname,
                surname = currentState.surname,
                birthdate = currentState.birthdate,
                nickname = currentState.nickname.ifBlank { null }
            )) {
                is Result.Success -> {
                    // Save JWT token (auto-login)
                    tokenManager.saveToken(registerResult.data.token)

                    // Get current user to determine role
                    when (val userResult = userRepository.getCurrentUser()) {
                        is Result.Success -> {
                            val isAdmin = userResult.data.userRole.userRole == "admin"
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRegistered = true,
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
                            error = registerResult.message
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
     * Reset registration state after successful navigation
     */
    fun resetRegisterState() {
        _uiState.update { it.copy(isRegistered = false) }
    }
}
