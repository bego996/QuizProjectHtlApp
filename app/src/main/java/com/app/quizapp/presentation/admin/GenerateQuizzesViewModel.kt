package com.app.quizapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.repository.LlmRepository
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for GenerateQuizzesScreen (Admin)
 * @param categories List of available topics for quiz generation
 * @param selectedCategory Selected topic for quiz generation
 * @param difficulty Selected difficulty level
 * @param isGenerating Whether quiz is being generated
 * @param error Error message if operation fails
 * @param generatedQuiz Generated quiz question (null if not generated yet)
 * @param isSaved Whether quiz was successfully saved
 */
data class GenerateQuizzesUiState(
    val categories: List<Topic> = emptyList(),
    val selectedCategory: Topic? = null,
    val topics: List<Topic> = emptyList(),
    val selectedTopic: Topic? = null,
    val subTopics: List<Topic> = emptyList(),
    val selectedSubtopic: Topic? = null,
    val difficulty: List<Difficulty> = emptyList(),
    val selectedDifficulty: Difficulty? = null,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val generatedQuiz: String? = null,
    val isSaved: Boolean = false
)

/**
 * ViewModel for Generate Quizzes screen (Admin only)
 * Handles AI quiz generation using LLM and saving to backend
 */
@HiltViewModel
class GenerateQuizzesViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val difficultyRepository: DifficultyRepository,
    private val llmRepository: LlmRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateQuizzesUiState())
    val uiState: StateFlow<GenerateQuizzesUiState> = _uiState.asStateFlow()

    init {
        loadTopics()
        loadDifficulties()
    }

    /**
     * Load all topics for quiz generation
     */
    private fun loadTopics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = topicRepository.getAllTopics()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            categories = result.data,
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

    private fun loadDifficulties(){
        viewModelScope.launch {
            _uiState.update {it.copy(isLoading = true, error = null)}

            when (val result = difficultyRepository.getAllDifficulties()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            difficulty = result.data,
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
     * Select topic for quiz generation
     * If the same topic is selected again, deselect it
     */
    fun selectTopic(topic: Topic) {
        _uiState.update {
            it.copy(selectedCategory = if (it.selectedCategory == topic) null else topic)
        }
    }

    /**
     * Set difficulty level
     */
    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    /**
     * Generate quiz using AI/LLM
     * TODO: Implement actual LLM API call when LlmRepository methods are defined
     */
    fun generateQuiz() {
        val currentState = _uiState.value

        if (currentState.selectedCategory == null) {
            _uiState.update { it.copy(error = "Please select a topic") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null, generatedQuiz = null) }

            // TODO: Implement actual LLM call
            // For now, show placeholder
            val generatedText = """
                Generated Quiz for ${currentState.selectedCategory.topic} (${currentState.selectedDifficulty?.mode ?: "No difficulty"}):
                Question: Sample AI-generated question
                A) Answer 1
                B) Answer 2
                C) Answer 3
                D) Answer 4
                Correct: A
            """.trimIndent()

            _uiState.update {
                it.copy(
                    isGenerating = false,
                    generatedQuiz = generatedText
                )
            }
        }
    }

    /**
     * Save generated quiz to backend
     * TODO: Parse generated quiz and create Question/Answer entities
     */
    fun saveQuiz() {
        val currentState = _uiState.value

        if (currentState.generatedQuiz == null) {
            _uiState.update { it.copy(error = "No quiz to save") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }

            // TODO: Parse generatedQuiz and create Question entity
            // TODO: Call questionRepository.createQuestion()
            // For now, just mark as saved
            _uiState.update {
                it.copy(
                    isGenerating = false,
                    isSaved = true,
                    error = "Quiz save not yet implemented (TODO)"
                )
            }
        }
    }

    /**
     * Reset save state
     */
    fun resetSaveState() {
        _uiState.update { it.copy(isSaved = false, generatedQuiz = null) }
    }
}
