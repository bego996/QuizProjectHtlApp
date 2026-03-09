package com.app.quizapp.presentation.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.repository.TopicRepository
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
 * UI state for TopicScreen
 * @param topics List of topics for selected category
 * @param parentCategory Parent category name
 * @param difficulties Available difficulties from backend (for difficulty dialog)
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class TopicUiState(
    val topics: List<Topic> = emptyList(),
    val parentCategory: String = "",
    val difficulties: List<Difficulty> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Topic screen
 * Loads topics for a specific parent category and available difficulties in parallel
 * @param savedStateHandle provides parentTopicId from navigation
 */
@HiltViewModel
class TopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val difficultyRepository: DifficultyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicUiState())
    val uiState: StateFlow<TopicUiState> = _uiState.asStateFlow()

    init {
        loadTopics(savedStateHandle["parentTopicId"])
    }

    /**
     * Load topics for a specific parent category and difficulties in parallel
     * @param parentTopicId Parent category ID
     */
    fun loadTopics(parentTopicId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val topicsDeferred = async { topicRepository.getAllTopics() }
            val difficultiesDeferred = async { difficultyRepository.getAllDifficulties() }

            when (val result = topicsDeferred.await()) {
                is Result.Success -> {
                    val filteredTopics = if (parentTopicId != null) {
                        result.data.filter { it.parentTopic?.topicId == parentTopicId }
                    } else {
                        result.data.filter { it.parentTopic != null }
                    }
                    val difficulties = when (val dr = difficultiesDeferred.await()) {
                        is Result.Success -> dr.data
                        is Result.Error -> emptyList()
                    }
                    _uiState.update {
                        it.copy(
                            topics = filteredTopics,
                            parentCategory = filteredTopics.firstOrNull()?.parentTopic?.topic ?: "Topics",
                            difficulties = difficulties,
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
