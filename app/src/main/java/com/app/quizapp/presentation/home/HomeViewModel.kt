package com.app.quizapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * UI state for HomeScreen
 * @param userName User's display name (nickname or email)
 * @param isAdmin Whether user has admin role
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class HomeUiState(
    val userName: String = "",
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Home screen
 * Loads current user information and determines role
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    /**
     * Load current user information
     * Sets userName (nickname if available, otherwise email)
     * and isAdmin flag based on user role
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    val user = result.data
                    // Use nickname if available, otherwise use email
                    val displayName = user.nickname?.takeIf { it.isNotBlank() } ?: user.email
                    val isAdmin = user.userRole.userRole == "admin"

                    _uiState.update {
                        it.copy(
                            userName = displayName,
                            isAdmin = isAdmin,
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
     * Refresh user information
     */
    fun refresh() {
        loadUserInfo()
    }
}
