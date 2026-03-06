package com.app.quizapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.repository.QuestionRepository
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
 * UI state for ActiveQuizScreen (Admin)
 * @param quizzes List of active/unreviewed quizzes
 * @param answersMap Map of questionId to list of answers
 * @param userNamesMap Map of userId to full user name (firstname + surname)
 * @param expandedQuizIds Set of questionIds that are currently expanded
 * @param isLoading Whether data is being loaded
 * @param error Error message if operation fails
 */
data class ActiveQuizUiState(
    val quizzes: List<Question> = emptyList(),
    val answersMap: Map<Int, List<Answer>> = emptyMap(),
    val userNamesMap: Map<Int, String> = emptyMap(),
    val expandedQuizIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Active Quiz screen (Admin only)
 * Shows active/unreviewed quizzes and handles quiz deletion
 */
@HiltViewModel
class ActiveQuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveQuizUiState())
    val uiState: StateFlow<ActiveQuizUiState> = _uiState.asStateFlow()

    init {
        loadQuizzes()
    }

    /**
     * Load all active/unreviewed quizzes and user names for reviewers
     * TODO: Add status filter when statusId constants are defined
     */
    private fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = questionRepository.getAllQuestions()) {
                is Result.Success -> {
                    // TODO: Filter by status (e.g., statusId = ACTIVE or UNREVIEWED)
                    // For now, show all questions
                    _uiState.update {
                        it.copy(
                            quizzes = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    // Load user names for all reviewedBy IDs
                    loadReviewerNames(result.data)
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
     * Load user names for all reviewers
     * @param questions List of questions to extract reviewer IDs from
     */
    private fun loadReviewerNames(questions: List<Question>) {
        viewModelScope.launch {
            // Collect all unique reviewer IDs
            val reviewerIds = questions
                .mapNotNull { it.reviewedBy }
                .filter { it > 0 }
                .distinct()

            // Load user data for each reviewer ID
            val userNamesMap = mutableMapOf<Int, String>()
            reviewerIds.forEach { userId ->
                when (val result = userRepository.getUserById(userId)) {
                    is Result.Success -> {
                        val user = result.data
                        userNamesMap[userId] = "${user.firstname} ${user.surname}"
                    }
                    is Result.Error -> {
                        // If loading fails, keep the ID without name
                        // Error is silently ignored to not disrupt the UI
                    }
                }
            }

            // Update UI state with loaded names
            _uiState.update {
                it.copy(userNamesMap = userNamesMap)
            }
        }
    }

    /**
     * Delete quiz by ID (admin only)
     */
    fun deleteQuiz(questionId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (questionRepository.deleteQuestion(questionId)) {
                is Result.Success -> {
                    // Reload quizzes after deletion
                    loadQuizzes()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to delete quiz"
                        )
                    }
                }
            }
        }
    }

    /**
     * Refresh quizzes list
     */
    fun refresh() {
        loadQuizzes()
    }

    /**
     * Toggle expanded state for a quiz card and load answers if not loaded yet
     * @param questionId The question ID to toggle
     */
    fun toggleQuizExpansion(questionId: Int) {
        val isCurrentlyExpanded = _uiState.value.expandedQuizIds.contains(questionId)

        if (isCurrentlyExpanded) {
            // Collapse the card
            _uiState.update {
                it.copy(expandedQuizIds = it.expandedQuizIds - questionId)
            }
        } else {
            // Expand the card and load answers if not loaded yet
            _uiState.update {
                it.copy(expandedQuizIds = it.expandedQuizIds + questionId)
            }

            // Load answers if not already loaded
            if (!_uiState.value.answersMap.containsKey(questionId)) {
                loadAnswersForQuestion(questionId)
            }
        }
    }

    /**
     * Load answers for a specific question
     * @param questionId The question ID to load answers for
     */
    private fun loadAnswersForQuestion(questionId: Int) {
        viewModelScope.launch {
            when (val result = answerRepository.getAllAnswersByQuestionId(questionId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            answersMap = it.answersMap + (questionId to result.data)
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = "Failed to load answers: ${result.message}")
                    }
                }
            }
        }
    }
}
