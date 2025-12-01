package com.app.quizapp.presentation.userquestion

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeUserQuestionRepository
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
class UserQuestionViewModelTest {

    private lateinit var fakeRepository: FakeUserQuestionRepository
    private lateinit var viewModel: UserQuestionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserQuestionRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty userQuestions list and not loading`() = runTest {
        viewModel = UserQuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userQuestions).hasSize(2)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUserQuestions should emit loading state then success with userQuestions`() = runTest {
        viewModel = UserQuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllUserQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userQuestions).isNotEmpty()
            assertThat(successState.userQuestions).hasSize(2)
            assertThat(successState.userQuestions[0].score).isEqualTo(100)
            assertThat(successState.userQuestions[0].user.firstname).isEqualTo("Max")
            assertThat(successState.userQuestions[0].question.questionText).isEqualTo("What is 2 + 2?")
            assertThat(successState.userQuestions[1].score).isEqualTo(85)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUserQuestions should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = UserQuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.userQuestions).isEmpty()
        }
    }

    @Test
    fun `loadAllUserQuestions should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = UserQuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllUserQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.userQuestions).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllUserQuestions calls should update state correctly`() = runTest {
        viewModel = UserQuestionViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllUserQuestions()
            skipItems(2)

            fakeRepository.userQuestionsToReturn = emptyList()

            viewModel.loadAllUserQuestions()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userQuestions).isEmpty()
        }
    }
}
