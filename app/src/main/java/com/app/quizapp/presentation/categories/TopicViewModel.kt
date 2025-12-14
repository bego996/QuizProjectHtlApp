package com.app.quizapp.presentation.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.Topic
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
 * UI state for TopicScreen
 * @param topics List of topics for selected category
 * @param parentCategory Parent category name
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class TopicUiState(
    val topics: List<Topic> = emptyList(),
    val parentCategory: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Topic screen
 * Loads topics for a specific parent category
 * Note: Currently loads all topics and filters client-side
 * TODO: Add parentTopicId parameter to navigation when implementing dynamic routing
 */
@HiltViewModel
class TopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicUiState())
    val uiState: StateFlow<TopicUiState> = _uiState.asStateFlow()

    init {
        // TODO: Get parentTopicId from navigation args when implementing dynamic routing
        // For now, load all topics
        loadTopics()
    }

    /**
     * Load topics for a specific parent category
     * @param parentTopicId Parent category ID (optional for now)
     */
    fun loadTopics(parentTopicId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = topicRepository.getAllTopics()) {
                is Result.Success -> {
                    // Filter topics by parent if parentTopicId is provided
                    val filteredTopics = if (parentTopicId != null) {
                        result.data.filter { it.parentTopic?.topicId == parentTopicId }
                    } else {
                        // For demo: show topics that have a parent (not root categories)
                        result.data.filter { it.parentTopic != null }
                    }

                    _uiState.update {
                        it.copy(
                            topics = filteredTopics,
                            parentCategory = filteredTopics.firstOrNull()?.parentTopic?.topic ?: "Topics",
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
