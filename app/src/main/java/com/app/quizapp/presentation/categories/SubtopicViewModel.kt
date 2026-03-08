package com.app.quizapp.presentation.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Display model for a single subtopic row – no Compose types, safe to store in ViewModel
 * @param topicId Unique topic ID (used as stable LazyColumn key)
 * @param name Display name
 * @param colorIndex Index into the color palette (assigned by position)
 * @param unsolvedCount Questions not yet solved by the current user for this difficulty
 */
data class SubtopicDisplayItem(
    val topicId: Int,
    val name: String,
    val colorIndex: Int,
    val unsolvedCount: Int
)

/**
 * UI state for SubtopicScreen
 * @param displayItems Ready-to-render subtopic rows including unsolved counts
 * @param parentTopic Parent topic name
 * @param difficultyId Selected difficulty filter (0 = Mixed/all difficulties)
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class SubtopicUiState(
    val displayItems: List<SubtopicDisplayItem> = emptyList(),
    val parentTopic: String = "",
    val difficultyId: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Subtopic screen
 * Loads subtopics for a specific parent topic and calculates
 * unsolved question counts filtered by difficulty for the current user
 * @param savedStateHandle provides parentTopicId and difficultyId from navigation
 */
@HiltViewModel
class SubtopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubtopicUiState())
    val uiState: StateFlow<SubtopicUiState> = _uiState.asStateFlow()

    init {
        // difficultyId = 0 means "Mixed" (no filter)
        val difficultyId: Int = savedStateHandle["difficultyId"] ?: 0
        loadSubtopics(
            parentTopicId = savedStateHandle["parentTopicId"],
            difficultyId = difficultyId
        )
    }

    /**
     * Load subtopics, then fetch question counts and quiz attempts in parallel.
     * @param parentTopicId Parent topic ID
     * @param difficultyId Difficulty filter ID; 0 = no filter (Mixed)
     */
    fun loadSubtopics(parentTopicId: Int? = null, difficultyId: Int = 0) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = topicRepository.getAllTopics()) {
                is Result.Success -> {
                    val filteredSubtopics = if (parentTopicId != null) {
                        result.data.filter { it.parentTopic?.topicId == parentTopicId }
                    } else {
                        result.data.filter { it.parentTopic != null }
                    }

                    // null means no filter (Mixed); pass actual ID only for specific difficulties
                    val filterById: Int? = if (difficultyId != 0) difficultyId else null

                    // Load quiz attempts and filtered question lists in parallel
                    val quizAttemptsDeferred = async { userRepository.getQuizAttempts() }
                    val questionsByTopicDeferred = filteredSubtopics.map { subtopic ->
                        subtopic.topicId to async {
                            questionRepository.getAllQuestions(
                                topicId = subtopic.topicId,
                                difficultyId = filterById
                            )
                        }
                    }

                    // All questionIds the user has already solved (across all topics/difficulties)
                    val solvedQuestionIds: Set<Int> = when (val qa = quizAttemptsDeferred.await()) {
                        is Result.Success -> qa.data.map { it.question.questionId }.toSet()
                        is Result.Error -> emptySet()
                    }

                    // Build display items: unsolved = questions in filtered list not yet solved
                    val displayItems = questionsByTopicDeferred.mapIndexed { index, (topicId, deferred) ->
                        val filteredQuestions = when (val qr = deferred.await()) {
                            is Result.Success -> qr.data
                            is Result.Error -> emptyList()
                        }
                        val unsolvedCount = filteredQuestions.count { it.questionId !in solvedQuestionIds }
                        SubtopicDisplayItem(
                            topicId = topicId,
                            name = filteredSubtopics[index].topic,
                            colorIndex = index,
                            unsolvedCount = unsolvedCount
                        )
                    }

                    _uiState.update {
                        it.copy(
                            displayItems = displayItems,
                            parentTopic = filteredSubtopics.firstOrNull()?.parentTopic?.topic ?: "Subtopics",
                            difficultyId = difficultyId,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}
