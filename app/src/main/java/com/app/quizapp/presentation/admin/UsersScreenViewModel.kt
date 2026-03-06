package com.app.quizapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for UsersScreen (Admin)
 * @param users List of all users
 * @param isLoading Whether data is being loaded
 * @param error Error message if operation fails
 */
data class UsersUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Users screen (Admin only)
 * Loads all users and handles user deletion and quiz reset
 */
@HiltViewModel
class UsersScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userQuestionRepository: UserQuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadAllUsers()
    }

    /**
     * Load all users (admin only)
     */
    private fun loadAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = userRepository.getAllUsers()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            users = result.data,
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
     * Delete user by ID (admin only)
     */
    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (userRepository.deleteUser(userId)) {
                is Result.Success -> {
                    // Reload users after deletion
                    loadAllUsers()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to delete user"
                        )
                    }
                }
            }
        }
    }

    /**
     * Reset all quiz attempts for a specific user (admin only)
     * Deletes all UserQuestions associated with the user
     */
    fun resetUserQuizzes(userId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (userQuestionRepository.deleteUserQuestionsByUserId(userId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to reset user quizzes"
                        )
                    }
                }
            }
        }
    }

    /**
     * Refresh users list
     */
    fun refresh() {
        loadAllUsers()
    }
}
