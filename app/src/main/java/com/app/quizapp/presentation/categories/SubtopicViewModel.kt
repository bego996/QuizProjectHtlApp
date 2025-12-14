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
 * UI state for SubtopicScreen
 * @param subtopics List of subtopics for selected topic
 * @param parentTopic Parent topic name
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class SubtopicUiState(
    val subtopics: List<Topic> = emptyList(),
    val parentTopic: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Subtopic screen
 * Loads subtopics for a specific parent topic
 * Note: Currently loads all topics and filters client-side
 * TODO: Add parentTopicId parameter to navigation when implementing dynamic routing
 */
@HiltViewModel
class SubtopicViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubtopicUiState())
    val uiState: StateFlow<SubtopicUiState> = _uiState.asStateFlow()

    init {
        // TODO: Get parentTopicId from navigation args when implementing dynamic routing
        // For now, load all subtopics
        loadSubtopics()
    }

    /**
     * Load subtopics for a specific parent topic
     * @param parentTopicId Parent topic ID (optional for now)
     */
    fun loadSubtopics(parentTopicId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = topicRepository.getAllTopics()) {
                is Result.Success -> {
                    // Filter subtopics by parent if parentTopicId is provided
                    val filteredSubtopics = if (parentTopicId != null) {
                        result.data.filter { it.parentTopic?.topicId == parentTopicId }
                    } else {
                        // For demo: show topics that have a parent (not root categories)
                        result.data.filter { it.parentTopic != null }
                    }

                    _uiState.update {
                        it.copy(
                            subtopics = filteredSubtopics,
                            parentTopic = filteredSubtopics.firstOrNull()?.parentTopic?.topic ?: "Subtopics",
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
