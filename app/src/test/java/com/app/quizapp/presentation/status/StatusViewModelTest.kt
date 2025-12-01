package com.app.quizapp.presentation.status

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeStatusRepository
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
class StatusViewModelTest {

    private lateinit var fakeRepository: FakeStatusRepository
    private lateinit var viewModel: StatusViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeStatusRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty statuses list and not loading`() = runTest {
        viewModel = StatusViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.statuses).hasSize(3)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllStatuses should emit loading state then success with statuses`() = runTest {
        viewModel = StatusViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllStatuses()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.statuses).isNotEmpty()
            assertThat(successState.statuses).hasSize(3)
            assertThat(successState.statuses[0].text).isEqualTo("Active")
            assertThat(successState.statuses[1].text).isEqualTo("Pending")
            assertThat(successState.statuses[2].text).isEqualTo("Inactive")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllStatuses should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = StatusViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.statuses).isEmpty()
        }
    }

    @Test
    fun `loadAllStatuses should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = StatusViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllStatuses()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.statuses).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllStatuses calls should update state correctly`() = runTest {
        viewModel = StatusViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllStatuses()
            skipItems(2)

            fakeRepository.statusesToReturn = emptyList()

            viewModel.loadAllStatuses()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.statuses).isEmpty()
        }
    }
}
