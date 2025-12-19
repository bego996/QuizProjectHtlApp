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
 * Unit tests for SubtopicViewModel
 * Tests loading and filtering subtopics by parent topic
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SubtopicViewModelTest {

    private lateinit var topicRepository: FakeTopicRepository
    private lateinit var viewModel: SubtopicViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val parentTopic = Topic(
        topicId = 2,
        topic = "Algebra",
        parentTopic = Topic(1, "Mathematics", null)
    )

    private val subtopic1 = Topic(
        topicId = 3,
        topic = "Linear Algebra",
        parentTopic = parentTopic
    )

    private val subtopic2 = Topic(
        topicId = 4,
        topic = "Abstract Algebra",
        parentTopic = parentTopic
    )

    private val otherTopicSubtopic = Topic(
        topicId = 5,
        topic = "Trigonometry",
        parentTopic = Topic(6, "Geometry", null)
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
    fun `init with parentTopicId filters subtopics correctly`() = runTest {
        // Given: Subtopics exist
        topicRepository.addTestTopic(parentTopic)
        topicRepository.addTestTopic(subtopic1)
        topicRepository.addTestTopic(subtopic2)
        topicRepository.addTestTopic(otherTopicSubtopic)

        // When: ViewModel is created with parentTopicId
        val savedStateHandle = SavedStateHandle(mapOf("parentTopicId" to 2))
        viewModel = SubtopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should load only subtopics for that parent
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.subtopics).hasSize(2)
            assertThat(state.subtopics).contains(subtopic1)
            assertThat(state.subtopics).contains(subtopic2)
            assertThat(state.subtopics).doesNotContain(otherTopicSubtopic)
            assertThat(state.parentTopic).isEqualTo("Algebra")
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init without parentTopicId shows all subtopics with parent`() = runTest {
        // Given: Subtopics exist
        topicRepository.addTestTopic(subtopic1)
        topicRepository.addTestTopic(subtopic2)

        // When: ViewModel is created without parentTopicId
        val savedStateHandle = SavedStateHandle()
        viewModel = SubtopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should show all topics that have a parent
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.subtopics).hasSize(2)
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        topicRepository.shouldReturnError = true
        topicRepository.errorMessage = "Network error"

        // When: ViewModel is created
        val savedStateHandle = SavedStateHandle(mapOf("parentTopicId" to 2))
        viewModel = SubtopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            assertThat(state.subtopics).isEmpty()
        }
    }

    // ========== LoadSubtopics Tests ==========

    @Test
    fun `loadSubtopics can be called manually with different parentId`() = runTest {
        // Given: Subtopics exist and ViewModel initialized
        topicRepository.addTestTopic(subtopic1)
        val savedStateHandle = SavedStateHandle()
        viewModel = SubtopicViewModel(topicRepository, savedStateHandle)
        advanceUntilIdle()

        // When: loadSubtopics is called with specific parentId
        topicRepository.addTestTopic(subtopic2)
        viewModel.loadSubtopics(parentTopicId = 2)
        advanceUntilIdle()

        // Then: Should reload with filter
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.subtopics).contains(subtopic1)
        }
    }
}
