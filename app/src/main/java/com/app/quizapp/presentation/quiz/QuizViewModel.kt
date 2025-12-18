package com.app.quizapp.presentation.quiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
 * UI state for QuizScreen
 * @param questions List of quiz questions (max 5)
 * @param currentQuestionIndex Current question index (0-based)
 * @param selectedAnswerId Selected answer ID for current question
 * @param score Current score
 * @param isLoading Whether quiz data is being loaded
 * @param error Error message if loading fails
 * @param isQuizComplete Whether quiz is finished
 * @param userAnswers Map of user's answers: questionId -> Pair(answerId, isCorrect)
 * @param isSavingToBackend Whether quiz results are being saved to backend
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
    val userAnswers: Map<Int, Pair<Int, Boolean>> = emptyMap(), // questionId -> (answerId, isCorrect)
    val isSavingToBackend: Boolean = false
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
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadDailyQuiz(savedStateHandle["subTopicId"])
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

            val questionsResult: Result<List<Question>>;

            // Load all questions if no param for topicId passed then load all question to pick randoms.
            if (topicId == null || topicId == 0){
                questionsResult = questionRepository.getAllQuestions()
            }else{
                questionsResult = questionRepository.getAllQuestions(topicId = topicId)
            }

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
            Log.e("QuizViewModel","size of questions : ${questionsResult.data.size}")

            // Load all answers for each question
            val answersMap = mutableMapOf<Int, List<Answer>>()
            allQuestions.forEach { question ->
                when (val result = answerRepository.getAllAnswersByQuestionId(question.questionId)) {
                    is Result.Success -> {
                        answersMap[question.questionId] = result.data
                        Log.e("QuizViewModel","size of answers =${result.data.size}")
                    }
                    is Result.Error -> {
                        // Skip this question if answers fail to load
                        Log.e("QuizViewModel","Failed to fetch answers for the question id:${question.questionId}")
                    }
                }
            }
            Log.e("QuizViewModel","size of answers : ${answersMap.size}")

            // Check if any answers were loaded
            if (answersMap.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load answers for questions"
                    )
                }
                return@launch
            }

            // Load quiz attempts to filter out completed questions
            val attemptsResult = userRepository.getQuizAttempts()
            val completedQuestionIds = if (attemptsResult is Result.Success) {
                // Filter questions that were answered correctly (score > 0)
                // Backend uses score: 100 = correct, 0 = incorrect
                attemptsResult.data
                    .filter { it.score > 0 }
                    .map { it.question.questionId }
                    .toSet()
            } else {
                emptySet()
            }

            // Filter uncompleted questions that have answers loaded
            val uncompletedQuestions = allQuestions.filter { question ->
                question.questionId !in completedQuestionIds &&
                answersMap.containsKey(question.questionId) &&
                answersMap[question.questionId]?.isNotEmpty() == true
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
     * Stores answer locally, submits to backend only when quiz is complete
     */
    fun submitAnswer() {
        val currentState = _uiState.value
        val selectedAnswerId = currentState.selectedAnswerId

        if (selectedAnswerId == null) {
            _uiState.update { it.copy(error = "Please select an answer") }
            return
        }

        // Check if answer is correct
        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val answers = currentState.answers[currentQuestion.questionId] ?: emptyList()
        val selectedAnswer = answers.find { it.answerId == selectedAnswerId }
        val isCorrect = selectedAnswer?.correct == true

        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        // Store answer locally
        val updatedUserAnswers = currentState.userAnswers.toMutableMap()
        updatedUserAnswers[currentQuestion.questionId] = Pair(selectedAnswerId, isCorrect)

        // Move to next question or complete quiz
        val nextIndex = currentState.currentQuestionIndex + 1
        if (nextIndex >= currentState.questions.size) {
            // Quiz complete - update state and submit to backend
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    score = newScore,
                    isQuizComplete = true,
                    selectedAnswerId = null,
                    userAnswers = updatedUserAnswers
                )
            }
            // Submit all answers to backend
            completeQuiz()
        } else {
            // Move to next question
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    score = newScore,
                    selectedAnswerId = null,
                    userAnswers = updatedUserAnswers
                )
            }
        }
    }

    /**
     * Submit all quiz answers to backend
     * Called when quiz is complete
     */
    private fun completeQuiz() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingToBackend = true) }

            // Submit each answered question to backend
            currentState.userAnswers.forEach { (questionId, answerData) ->
                val (answerId, _) = answerData

                // Start quiz for this question
                when (val startResult = userRepository.startQuiz(questionId)) {
                    is Result.Success -> {
                        val userQuestionId = startResult.data.userQuestionId

                        // Submit the answer
                        userRepository.submitAnswer(userQuestionId, answerId)
                    }
                    is Result.Error -> {
                        // Log error but continue with other questions
                        // Could handle this more explicitly if needed
                    }
                }
            }

            _uiState.update { it.copy(isSavingToBackend = false) }
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
