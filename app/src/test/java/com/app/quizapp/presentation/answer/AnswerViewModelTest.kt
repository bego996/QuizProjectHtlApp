package com.app.quizapp.presentation.answer

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeAnswerRepository
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
class AnswerViewModelTest {

    private lateinit var fakeRepository: FakeAnswerRepository
    private lateinit var viewModel: AnswerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAnswerRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty answers list and not loading`() = runTest {
        viewModel = AnswerViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.answers).hasSize(2)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllAnswers should emit loading state then success with answers`() = runTest {
        viewModel = AnswerViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllAnswers()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.answers).isNotEmpty()
            assertThat(successState.answers).hasSize(2)
            assertThat(successState.answers[0].text).isEqualTo("4")
            assertThat(successState.answers[0].correct).isTrue()
            assertThat(successState.answers[1].text).isEqualTo("5")
            assertThat(successState.answers[1].correct).isFalse()
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllAnswers should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = AnswerViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.answers).isEmpty()
        }
    }

    @Test
    fun `loadAllAnswers should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = AnswerViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllAnswers()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.answers).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllAnswers calls should update state correctly`() = runTest {
        viewModel = AnswerViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllAnswers()
            skipItems(2)

            fakeRepository.answersToReturn = emptyList()

            viewModel.loadAllAnswers()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.answers).isEmpty()
        }
    }
}
