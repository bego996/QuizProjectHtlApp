package com.app.quizapp.presentation.categories

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.app.quizapp.data.repository.fake.FakeTopicRepository
import com.app.quizapp.domain.model.Topic
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TopicViewModel
 * Tests loading and filtering topics by parent category
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {

    private lateinit var topicRepository: FakeTopicRepository
    private lateinit var viewModel: TopicViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val parentCategory = Topic(
        topicId = 1,
        topic = "Mathematics",
        parentTopic = null
    )

    private val topic1 = Topic(
        topicId = 2,
        topic = "Algebra",
        parentTopic = parentCategory
    )

    private val topic2 = Topic(
        topicId = 3,
        topic = "Geometry",
        parentTopic = parentCategory
    )

    private val otherCategoryTopic = Topic(
        topicId = 4,
        topic = "Physics",
        parentTopic = Topic(5, "Science", null)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        topicRepository = FakeTopicRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        topicRepository.clearTestData()
    }

    // ========== Init Tests with Parent ID ==========

    @Test
    fun `init with parentTopicId filters topics correctly`() = runTest {
        // Given: Topics exist
        topicRepository.addTestTopic(parentCategory)
        topicRepository.addTestTopic(topic1)
        topicRepository.addTestTopic(topic2)
        topicRepository.addTestTopic(otherCategoryTopic)

        // When: ViewModel is created with parentTopicId
        val savedStateHandle = SavedStateHandle(mapOf("parentTopicId" to 1))
        viewModel = TopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should load only topics for that parent
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.topics).hasSize(2)
            assertThat(state.topics).contains(topic1)
            assertThat(state.topics).contains(topic2)
            assertThat(state.topics).doesNotContain(otherCategoryTopic)
            assertThat(state.parentCategory).isEqualTo("Mathematics")
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init without parentTopicId shows all topics with parent`() = runTest {
        // Given: Topics exist
        topicRepository.addTestTopic(parentCategory)
        topicRepository.addTestTopic(topic1)
        topicRepository.addTestTopic(topic2)

        // When: ViewModel is created without parentTopicId
        val savedStateHandle = SavedStateHandle()
        viewModel = TopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should show all topics that have a parent
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.topics).hasSize(2)
            assertThat(state.topics).doesNotContain(parentCategory) // Root excluded
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        topicRepository.shouldReturnError = true
        topicRepository.errorMessage = "Network error"

        // When: ViewModel is created
        val savedStateHandle = SavedStateHandle(mapOf("parentTopicId" to 1))
        viewModel = TopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            assertThat(state.topics).isEmpty()
        }
    }

    // ========== LoadTopics Tests ==========

    @Test
    fun `loadTopics can be called manually with different parentId`() = runTest {
        // Given: Topics exist and ViewModel initialized
        topicRepository.addTestTopic(topic1)
        val savedStateHandle = SavedStateHandle()
        viewModel = TopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // When: loadTopics is called with specific parentId
        topicRepository.addTestTopic(topic2)
        viewModel.loadTopics(parentTopicId = 1)
        advanceUntilIdle()

        // Then: Should reload with filter
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.topics).contains(topic1)
        }
    }
}
