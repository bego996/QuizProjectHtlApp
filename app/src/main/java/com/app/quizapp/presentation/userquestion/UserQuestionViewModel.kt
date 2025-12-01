package com.app.quizapp.presentation.userquestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserQuestionUiState(
    val userQuestions: List<UserQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserQuestionViewModel @Inject constructor(
    private val repository: UserQuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserQuestionUiState())
    val uiState: StateFlow<UserQuestionUiState> = _uiState.asStateFlow()

    init {
        loadAllUserQuestions()
    }

    fun loadAllUserQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getAllUserQuestions()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            userQuestions = result.data,
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
