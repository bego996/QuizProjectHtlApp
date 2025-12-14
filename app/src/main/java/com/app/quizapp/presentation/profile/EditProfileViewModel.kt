package com.app.quizapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.data.remote.dto.UpdateProfileRequestDto
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for EditProfileScreen
 * @param firstname User's first name
 * @param surname User's surname
 * @param nickname User's nickname (optional)
 * @param birthdate User's birthdate (format: dd.MM.yyyy or yyyy-MM-dd)
 * @param isLoading Whether profile is being loaded or updated
 * @param error Error message if operation fails
 * @param isSaved Whether profile was successfully saved
 */
data class EditProfileUiState(
    val firstname: String = "",
    val surname: String = "",
    val nickname: String = "",
    val birthdate: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)

/**
 * ViewModel for Edit Profile screen
 * Loads current profile and handles profile updates
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    /**
     * Load current user profile to pre-fill form fields
     */
    private fun loadCurrentProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    val user = result.data
                    _uiState.update {
                        it.copy(
                            firstname = user.firstname,
                            surname = user.surname,
                            nickname = user.nickname ?: "",
                            birthdate = user.birthdate,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
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
     * Update nickname input
     */
    fun onNicknameChange(nickname: String) {
        _uiState.update { it.copy(nickname = nickname, error = null) }
    }

    /**
     * Update birthdate input
     */
    fun onBirthdateChange(birthdate: String) {
        _uiState.update { it.copy(birthdate = birthdate, error = null) }
    }

    /**
     * Save profile updates
     * Validates input and calls updateCurrentUser endpoint
     */
    fun updateProfile() {
        val currentState = _uiState.value

        // Validate input
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

            val updateRequest = UpdateProfileRequestDto(
                firstname = currentState.firstname,
                surname = currentState.surname,
                nickname = currentState.nickname.ifBlank { null },
                birthdate = currentState.birthdate
            )

            when (val result = userRepository.updateCurrentUser(updateRequest.firstname,updateRequest.surname,updateRequest.nickname,updateRequest.birthdate)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaved = true,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Reset saved state after navigation
     */
    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
