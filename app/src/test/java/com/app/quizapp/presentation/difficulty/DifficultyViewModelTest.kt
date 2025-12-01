package com.app.quizapp.presentation.difficulty

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeDifficultyRepository
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
class DifficultyViewModelTest {

    private lateinit var fakeRepository: FakeDifficultyRepository
    private lateinit var viewModel: DifficultyViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeDifficultyRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty difficulties list and not loading`() = runTest {
        viewModel = DifficultyViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.difficulties).hasSize(3)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllDifficulties should emit loading state then success with difficulties`() = runTest {
        viewModel = DifficultyViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllDifficulties()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.difficulties).isNotEmpty()
            assertThat(successState.difficulties).hasSize(3)
            assertThat(successState.difficulties[0].mode).isEqualTo("Easy")
            assertThat(successState.difficulties[1].mode).isEqualTo("Medium")
            assertThat(successState.difficulties[2].mode).isEqualTo("Hard")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllDifficulties should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = DifficultyViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.difficulties).isEmpty()
        }
    }

    @Test
    fun `loadAllDifficulties should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = DifficultyViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllDifficulties()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.difficulties).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllDifficulties calls should update state correctly`() = runTest {
        viewModel = DifficultyViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllDifficulties()
            skipItems(2)

            fakeRepository.difficultiesToReturn = emptyList()

            viewModel.loadAllDifficulties()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.difficulties).isEmpty()
        }
    }
}
