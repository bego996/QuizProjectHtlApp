package com.app.quizapp.presentation.categories

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
 * UI state for CategoryScreen (root categories)
 * @param categories List of root categories (parentTopic == null)
 * @param isLoading Whether data is being loaded
 * @param error Error message if loading fails
 */
data class CategoryUiState(
    val categories: List<Topic> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for Category screen
 * Loads root categories (topics with no parent)
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Load root categories (topics where parentTopic == null)
     */
    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = topicRepository.getAllHighestTopics()) {
                is Result.Success -> {
                    // Filter for root categories (no parent)
                    val rootCategories = result.data.filter { it.parentTopic == null }
                    _uiState.update {
                        it.copy(
                            categories = rootCategories,
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
     * Refresh categories
     */
    fun refresh() {
        loadCategories()
    }
}
