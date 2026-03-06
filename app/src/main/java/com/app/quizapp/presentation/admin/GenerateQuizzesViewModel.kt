package com.app.quizapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.data.remote.dto.QuizResponseDto
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.repository.LlmRepository
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.repository.TopicRepository
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
 * Selection level for hierarchical topic selection
 */
enum class TopicSelectionLevel {
    CATEGORY,  // First level
    TOPIC,     // Second level
    SUBTOPIC   // Third level
}

/**
 * UI state for GenerateQuizzesScreen (Admin)
 * @param allTopics All topics loaded from repository (hierarchical structure)
 * @param difficulties All available difficulty levels
 * @param selectedDifficulty Currently selected difficulty
 * @param selectedCategory Selected category (root topic)
 * @param selectedTopic Selected topic (second level)
 * @param selectedSubtopic Selected subtopic (third level)
 * @param currentSelectionLevel Current level in topic selection flow
 * @param availableCategories Filtered list of root-level categories
 * @param availableTopics Filtered list of topics for selected category
 * @param availableSubtopics Filtered list of subtopics for selected topic
 * @param generatedQuizResponse Generated quiz from LLM
 * @param showDifficultyDialog Whether to show difficulty selection dialog
 * @param showTopicSelectionDialog Whether to show topic selection dialog
 * @param isGenerating Whether quiz is being generated
 * @param isLoading Whether initial data is loading
 * @param currentUserId ID of the currently logged in admin
 * @param error Error message if operation fails
 */
data class GenerateQuizzesUiState(
    val allTopics: List<Topic> = emptyList(),
    val difficulties: List<Difficulty> = emptyList(),
    val selectedDifficulty: Difficulty? = null,
    val selectedCategory: Topic? = null,
    val selectedTopic: Topic? = null,
    val selectedSubtopic: Topic? = null,
    val currentSelectionLevel: TopicSelectionLevel = TopicSelectionLevel.CATEGORY,
    val availableCategories: List<Topic> = emptyList(),
    val availableTopics: List<Topic> = emptyList(),
    val availableSubtopics: List<Topic> = emptyList(),
    val generatedQuizResponse: QuizResponseDto? = null,
    val showDifficultyDialog: Boolean = false,
    val showTopicSelectionDialog: Boolean = false,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = true,
    val currentUserId: Int? = null,
    val error: String? = null
)

/**
 * ViewModel for Generate Quizzes screen (Admin only)
 * Handles AI quiz generation using LLM with hierarchical topic selection
 */
@HiltViewModel
class GenerateQuizzesViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val difficultyRepository: DifficultyRepository,
    private val llmRepository: LlmRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateQuizzesUiState())
    val uiState: StateFlow<GenerateQuizzesUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    /**
     * Load all topics, difficulties and current user on initialization
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load current user (admin) ID
            when (val userResult = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    _uiState.update { it.copy(currentUserId = userResult.data.userId) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = userResult.message) }
                }
            }

            // Load difficulties
            when (val diffResult = difficultyRepository.getAllDifficulties()) {
                is Result.Success -> {
                    _uiState.update { it.copy(difficulties = diffResult.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = diffResult.message) }
                }
            }

            // Load topics
            when (val topicResult = topicRepository.getAllTopics()) {
                is Result.Success -> {
                    val allTopics = topicResult.data
                    val categories = filterRootTopics(allTopics)
                    _uiState.update {
                        it.copy(
                            allTopics = allTopics,
                            availableCategories = categories,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = topicResult.message
                        )
                    }
                }
            }
        }
    }

    // ========== Dialog Management ==========

    /**
     * Show difficulty selection dialog (Image 3)
     */
    fun showDifficultyDialog() {
        _uiState.update { it.copy(showDifficultyDialog = true) }
    }

    /**
     * Hide difficulty selection dialog
     */
    fun hideDifficultyDialog() {
        _uiState.update { it.copy(showDifficultyDialog = false) }
    }

    /**
     * Show topic selection dialog and reset to category level (Image 4)
     */
    fun showTopicSelectionDialog() {
        _uiState.update {
            it.copy(
                showTopicSelectionDialog = true,
                currentSelectionLevel = TopicSelectionLevel.CATEGORY
            )
        }
    }

    /**
     * Hide topic selection dialog
     */
    fun hideTopicSelectionDialog() {
        _uiState.update { it.copy(showTopicSelectionDialog = false) }
    }

    // ========== Difficulty Selection ==========

    /**
     * Select difficulty and close dialog
     */
    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update {
            it.copy(
                selectedDifficulty = difficulty,
                showDifficultyDialog = false
            )
        }
        // After difficulty selection, show topic selection dialog
        showTopicSelectionDialog()
    }

    /**
     * Select random difficulty and proceed to topic selection
     */
    fun selectRandomDifficulty() {
        val randomDifficulty = _uiState.value.difficulties.randomOrNull()
        if (randomDifficulty != null) {
            selectDifficulty(randomDifficulty)
        }
    }

    // ========== Topic Hierarchy Filtering ==========

    /**
     * Filter root-level topics (categories with no parent)
     */
    private fun filterRootTopics(allTopics: List<Topic>): List<Topic> {
        return allTopics.filter { it.parentTopic == null }
    }

    /**
     * Filter topics by parent topic
     */
    private fun filterTopicsByParent(allTopics: List<Topic>, parent: Topic): List<Topic> {
        return allTopics.filter { it.parentTopic?.topicId == parent.topicId }
    }

    // ========== Hierarchical Topic Selection ==========

    /**
     * Select category (first level) and update available topics
     */
    fun selectCategory(category: Topic) {
        val topics = filterTopicsByParent(_uiState.value.allTopics, category)
        _uiState.update {
            it.copy(
                selectedCategory = category,
                availableTopics = topics,
                currentSelectionLevel = TopicSelectionLevel.TOPIC,
                // Reset lower levels
                selectedTopic = null,
                selectedSubtopic = null,
                availableSubtopics = emptyList()
            )
        }
    }

    /**
     * Select random category and proceed to topic level
     */
    fun selectRandomCategory() {
        val randomCategory = _uiState.value.availableCategories.randomOrNull()
        if (randomCategory != null) {
            selectCategory(randomCategory)
        }
    }

    /**
     * Select topic (second level) and update available subtopics
     */
    fun selectTopic(topic: Topic) {
        val subtopics = filterTopicsByParent(_uiState.value.allTopics, topic)
        _uiState.update {
            it.copy(
                selectedTopic = topic,
                availableSubtopics = subtopics,
                currentSelectionLevel = TopicSelectionLevel.SUBTOPIC,
                // Reset lower level
                selectedSubtopic = null
            )
        }
    }

    /**
     * Select random topic and proceed to subtopic level
     */
    fun selectRandomTopic() {
        val randomTopic = _uiState.value.availableTopics.randomOrNull()
        if (randomTopic != null) {
            selectTopic(randomTopic)
        }
    }

    /**
     * Select subtopic (third level) and trigger quiz generation
     */
    fun selectSubtopic(subtopic: Topic) {
        _uiState.update {
            it.copy(
                selectedSubtopic = subtopic,
                showTopicSelectionDialog = false
            )
        }
        // Automatically generate quiz after full selection
        generateQuizWithSelection()
    }

    /**
     * Select random subtopic and generate quiz
     */
    fun selectRandomSubtopic() {
        val randomSubtopic = _uiState.value.availableSubtopics.randomOrNull()
        if (randomSubtopic != null) {
            selectSubtopic(randomSubtopic)
        }
    }

    /**
     * Go back one level in topic selection
     */
    fun goBackInSelection() {
        _uiState.update { state ->
            when (state.currentSelectionLevel) {
                TopicSelectionLevel.TOPIC -> {
                    state.copy(
                        currentSelectionLevel = TopicSelectionLevel.CATEGORY,
                        selectedCategory = null,
                        availableTopics = emptyList()
                    )
                }
                TopicSelectionLevel.SUBTOPIC -> {
                    state.copy(
                        currentSelectionLevel = TopicSelectionLevel.TOPIC,
                        selectedTopic = null,
                        availableSubtopics = emptyList()
                    )
                }
                else -> state
            }
        }
    }

    // ========== Quiz Generation ==========

    /**
     * Generate quiz with current selections (difficulty, category, topic, subtopic)
     */
    fun generateQuizWithSelection() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }

            val result = llmRepository.generateQuiz(
                categoryId = currentState.selectedCategory?.topicId,
                topicId = currentState.selectedTopic?.topicId,
                subtopicId = currentState.selectedSubtopic?.topicId,
                difficultyId = currentState.selectedDifficulty?.difficultyId
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generatedQuizResponse = result.data,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Start new quiz generation request
     * Opens difficulty selection dialog
     */
    fun startNewRequest() {
        // Reset selections
        _uiState.update {
            it.copy(
                selectedDifficulty = null,
                selectedCategory = null,
                selectedTopic = null,
                selectedSubtopic = null,
                currentSelectionLevel = TopicSelectionLevel.CATEGORY,
                availableTopics = emptyList(),
                availableSubtopics = emptyList()
            )
        }
        // Show difficulty dialog
        showDifficultyDialog()
    }

    /**
     * Apply generated quiz to database with admin's user ID as reviewedBy
     */
    fun applyQuizToDatabase() {
        val quiz = _uiState.value.generatedQuizResponse?.quiz
        val currentUserId = _uiState.value.currentUserId

        if (quiz == null) {
            _uiState.update { it.copy(error = "No quiz to save") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }

            val result = llmRepository.addQuizToDatabase(
                category = quiz.category,
                topic = quiz.topic,
                subtopic = quiz.subtopic,
                question = quiz.question,
                difficulty = quiz.difficulty,
                answers = quiz.answers,
                correctAnswer = quiz.correctAnswer,
                reviewedBy = currentUserId
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            error = null,
                            // Clear generated quiz after successful save
                            generatedQuizResponse = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
