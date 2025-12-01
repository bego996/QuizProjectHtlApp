package com.app.quizapp.presentation.question

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeQuestionRepository
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
class QuestionViewModelTest {

    private lateinit var fakeRepository: FakeQuestionRepository
    private lateinit var viewModel: QuestionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQuestionRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty questions list and not loading`() = runTest {
        viewModel = QuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.questions).hasSize(2)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllQuestions should emit loading state then success with questions`() = runTest {
        viewModel = QuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.questions).isNotEmpty()
            assertThat(successState.questions).hasSize(2)
            assertThat(successState.questions[0].questionText).isEqualTo("What is 2 + 2?")
            assertThat(successState.questions[1].questionText).isEqualTo("What is the capital of France?")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllQuestions should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = QuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.questions).isEmpty()
        }
    }

    @Test
    fun `loadAllQuestions should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = QuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.questions).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllQuestions calls should update state correctly`() = runTest {
        viewModel = QuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllQuestions()
            skipItems(2)

            fakeRepository.questionsToReturn = emptyList()

            viewModel.loadAllQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.questions).isEmpty()
        }
    }
}
