package com.app.quizapp.presentation.topic

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeTopicRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {

    private lateinit var fakeRepository: FakeTopicRepository
    private lateinit var viewModel: TopicViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTopicRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty topics list and not loading`() = runTest {
        viewModel = TopicViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.topics).hasSize(2)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllTopics should emit loading state then success with topics`() = runTest {
        viewModel = TopicViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllTopics()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.topics).isNotEmpty()
            assertThat(successState.topics).hasSize(2)
            assertThat(successState.topics[0].topic).isEqualTo("Mathematics")
            assertThat(successState.topics[1].topic).isEqualTo("Geography")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllTopics should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = TopicViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.topics).isEmpty()
        }
    }

    @Test
    fun `loadAllTopics should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = TopicViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllTopics()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.topics).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllTopics calls should update state correctly`() = runTest {
        viewModel = TopicViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllTopics()
            skipItems(2)

            fakeRepository.topicsToReturn = emptyList()

            viewModel.loadAllTopics()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.topics).isEmpty()
        }
    }
}
