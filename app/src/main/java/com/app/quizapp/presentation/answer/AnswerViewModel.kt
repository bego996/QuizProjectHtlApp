package com.app.quizapp.presentation.answer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State für den Answer-Screen
 */
data class AnswerUiState(
    val answers: List<Answer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel für den Answer-Screen
 *
 * Lädt alle Answers vom Backend und stellt sie für die UI bereit.
 */
@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val repository: AnswerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnswerUiState())
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()

    init {
        loadAllAnswers()
    }

    /**
     * Lädt alle Answers vom Backend
     */
    fun loadAllAnswers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getAllAnswers()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            answers = result.data,
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
