package com.app.quizapp.presentation.categories

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
 * Unit tests for CategoryViewModel
 * Tests loading and displaying root categories
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private lateinit var topicRepository: FakeTopicRepository
    private lateinit var viewModel: CategoryViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val rootCategory1 = Topic(
        topicId = 1,
        topic = "Mathematics",
        parentTopic = null
    )

    private val rootCategory2 = Topic(
        topicId = 2,
        topic = "Science",
        parentTopic = null
    )

    private val childTopic = Topic(
        topicId = 3,
        topic = "Algebra",
        parentTopic = rootCategory1
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

    // ========== Init Tests ==========

    @Test
    fun `init loads root categories successfully`() = runTest {
        // Given: Root categories exist
        topicRepository.addTestTopic(rootCategory1)
        topicRepository.addTestTopic(rootCategory2)
        topicRepository.addTestTopic(childTopic)

        // When: ViewModel is created
        viewModel = CategoryViewModel(topicRepository)
        advanceUntilIdle()

        // Then: Should load only root categories
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.categories).hasSize(2)
            assertThat(state.categories).contains(rootCategory1)
            assertThat(state.categories).contains(rootCategory2)
            assertThat(state.categories).doesNotContain(childTopic)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `init with no root categories returns empty list`() = runTest {
        // Given: Only child topics exist
        topicRepository.addTestTopic(childTopic)

        // When: ViewModel is created
        viewModel = CategoryViewModel(topicRepository)
        advanceUntilIdle()

        // Then: Should return empty list
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.categories).isEmpty()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        topicRepository.shouldReturnError = true
        topicRepository.errorMessage = "Network error"

        // When: ViewModel is created
        viewModel = CategoryViewModel(topicRepository)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            assertThat(state.categories).isEmpty()
        }
    }

    // ========== Refresh Tests ==========

    @Test
    fun `refresh reloads categories`() = runTest {
        // Given: Initial categories loaded
        topicRepository.addTestTopic(rootCategory1)
        viewModel = CategoryViewModel(topicRepository)
        advanceUntilIdle()

        // When: New category added and refresh is called
        topicRepository.addTestTopic(rootCategory2)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should reload with new category
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.categories).hasSize(2)
            assertThat(state.categories).contains(rootCategory2)
        }
    }

    @Test
    fun `refresh clears previous error`() = runTest {
        // Given: Initial load with error
        topicRepository.shouldReturnError = true
        topicRepository.errorMessage = "Network error"
        viewModel = CategoryViewModel(topicRepository)
        advanceUntilIdle()

        // When: Error is fixed and refresh is called
        topicRepository.shouldReturnError = false
        topicRepository.addTestTopic(rootCategory1)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should clear error and load categories
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            assertThat(state.categories).isNotEmpty()
        }
    }
}
