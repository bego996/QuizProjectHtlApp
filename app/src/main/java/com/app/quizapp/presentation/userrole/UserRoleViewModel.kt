package com.app.quizapp.presentation.userrole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserRoleUiState(
    val userRoles: List<UserRole> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserRoleViewModel @Inject constructor(
    private val repository: UserRoleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserRoleUiState())
    val uiState: StateFlow<UserRoleUiState> = _uiState.asStateFlow()

    init {
        loadAllUserRoles()
    }

    fun loadAllUserRoles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getAllUserRoles()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            userRoles = result.data,
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
}
