package com.app.quizapp.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.data.remote.dto.StartQuizRequestDto
import com.app.quizapp.data.remote.dto.SubmitAnswerRequestDto
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
 * UI state for QuizScreen
 * @param questions List of quiz questions (max 5)
 * @param currentQuestionIndex Current question index (0-based)
 * @param selectedAnswerId Selected answer ID for current question
 * @param score Current score
 * @param isLoading Whether quiz data is being loaded
 * @param error Error message if loading fails
 * @param isQuizComplete Whether quiz is finished
 * @param currentUserQuestionId Current UserQuestion ID for backend tracking
 */
data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val answers: Map<Int, List<Answer>> = emptyMap(), // questionId -> List of answers
    val currentQuestionIndex: Int = 0,
    val selectedAnswerId: Int? = null,
    val score: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isQuizComplete: Boolean = false,
    val currentUserQuestionId: Int? = null
)

/**
 * ViewModel for Quiz screen
 * Implements Daily Quiz logic:
 * - Loads all questions and answers
 * - Filters out already completed questions (via quiz attempts)
 * - Randomly selects 5 uncompleted questions
 * - Tracks quiz progress with backend via startQuiz/submitAnswer
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadDailyQuiz()
    }

    /**
     * Load Daily Quiz: 5 random uncompleted questions
     * 1. Load all questions
     * 2. Load all answers
     * 3. Load quiz attempts (completed questions)
     * 4. Filter out completed questions
     * 5. Randomly select 5
     * 6. Start quiz with first question
     */
    private fun loadDailyQuiz(topicId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load all questions
            val questionsResult = questionRepository.getAllQuestions(topicId = topicId)
            if (questionsResult is Result.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load questions: ${questionsResult.message}"
                    )
                }
                return@launch
            }
            val allQuestions = (questionsResult as Result.Success).data

            // Load all answers
            val answersResult = answerRepository.getAllAnswers()
            if (answersResult is Result.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load answers: ${answersResult.message}"
                    )
                }
                return@launch
            }
            val allAnswers = (answersResult as Result.Success).data

            // Group answers by questionId
            val answersMap = allAnswers.groupBy { it.question.questionId }

            // Load quiz attempts to filter out completed questions
            val attemptsResult = userRepository.getQuizAttempts()
            val completedQuestionIds = if (attemptsResult is Result.Success) {
                attemptsResult.data.map { it.question.questionId }.toSet()
            } else {
                emptySet()
            }

            // Filter uncompleted questions
            val uncompletedQuestions = allQuestions.filter { question ->
                question.questionId !in completedQuestionIds
            }

            // Randomly select up to 5 questions
            val quizQuestions = uncompletedQuestions.shuffled().take(5)

            if (quizQuestions.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No questions available. You've completed all questions!"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    questions = quizQuestions,
                    answers = answersMap,
                    currentQuestionIndex = 0,
                    isLoading = false,
                    error = null
                )
            }

            // Start quiz with first question
            startQuizQuestion()
        }
    }

    /**
     * Start quiz question on backend
     * Creates UserQuestion entry for tracking
     */
    private fun startQuizQuestion() {
        val currentState = _uiState.value
        if (currentState.questions.isEmpty()) return

        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]

        viewModelScope.launch {
            when (val result = userRepository.startQuiz(StartQuizRequestDto(currentQuestion.questionId).questionId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(currentUserQuestionId = result.data.userQuestionId)
                    }
                }
                is Result.Error -> {
                    // Continue quiz even if backend tracking fails
                    _uiState.update {
                        it.copy(error = "Warning: Quiz tracking failed")
                    }
                }
            }
        }
    }

    /**
     * Select an answer
     */
    fun selectAnswer(answerId: Int) {
        _uiState.update { it.copy(selectedAnswerId = answerId) }
    }

    /**
     * Submit answer and move to next question
     */
    fun submitAnswer() {
        val currentState = _uiState.value
        val selectedAnswerId = currentState.selectedAnswerId

        if (selectedAnswerId == null) {
            _uiState.update { it.copy(error = "Please select an answer") }
            return
        }

        viewModelScope.launch {
            // Submit answer to backend if userQuestionId is available
            currentState.currentUserQuestionId?.let { userQuestionId ->
                userRepository.submitAnswer(
                    userQuestionId,
                    SubmitAnswerRequestDto(selectedAnswerId).answerId
                )
            }

            // Check if answer is correct and update score
            val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
            val answers = currentState.answers[currentQuestion.questionId] ?: emptyList()
            val selectedAnswer = answers.find { it.answerId == selectedAnswerId }
            val isCorrect = selectedAnswer?.correct == true

            val newScore = if (isCorrect) currentState.score + 1 else currentState.score

            // Move to next question or complete quiz
            val nextIndex = currentState.currentQuestionIndex + 1
            if (nextIndex >= currentState.questions.size) {
                // Quiz complete
                _uiState.update {
                    it.copy(
                        score = newScore,
                        isQuizComplete = true,
                        selectedAnswerId = null
                    )
                }
            } else {
                // Move to next question
                _uiState.update {
                    it.copy(
                        currentQuestionIndex = nextIndex,
                        score = newScore,
                        selectedAnswerId = null,
                        currentUserQuestionId = null
                    )
                }
                startQuizQuestion()
            }
        }
    }

    /**
     * Get current question
     */
    fun getCurrentQuestion(): Question? {
        val state = _uiState.value
        return if (state.questions.isNotEmpty() && state.currentQuestionIndex < state.questions.size) {
            state.questions[state.currentQuestionIndex]
        } else null
    }

    /**
     * Get answers for current question
     */
    fun getCurrentAnswers(): List<Answer> {
        val currentQuestion = getCurrentQuestion() ?: return emptyList()
        return _uiState.value.answers[currentQuestion.questionId] ?: emptyList()
    }

    /**
     * Reset quiz completion state
     */
    fun resetQuizComplete() {
        _uiState.update { it.copy(isQuizComplete = false) }
    }
}
