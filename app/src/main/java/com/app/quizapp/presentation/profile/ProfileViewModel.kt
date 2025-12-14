package com.app.quizapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.User
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
 * UI state for Profile screens (Overview and Statistics)
 * @param user Current user data
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * Shared ViewModel for ProfileOverviewScreen and ProfileStatisticsScreen
 * Loads current user profile information
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * Load current user profile
     */
    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            user = result.data,
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
     * Refresh user profile
     */
    fun refresh() {
        loadProfile()
    }

    /**
     * Get display name (nickname if available, otherwise full name or email)
     */
    fun getDisplayName(): String {
        val user = _uiState.value.user ?: return ""
        return user.nickname?.takeIf { it.isNotBlank() }
            ?: "${user.firstname} ${user.surname}".takeIf { it.isNotBlank() }
            ?: user.email
    }

    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean {
        return _uiState.value.user?.userRole?.userRole == "admin"
    }
}
