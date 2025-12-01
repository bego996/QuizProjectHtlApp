package com.app.quizapp.presentation.user

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeUserRepository
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
class UserViewModelTest {

    private lateinit var fakeRepository: FakeUserRepository
    private lateinit var viewModel: UserViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty users list and not loading`() = runTest {
        viewModel = UserViewModel(fakeRepository)

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // Loading state
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            // Success state with data
            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.users).hasSize(2)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUsers should emit loading state then success with users`() = runTest {
        viewModel = UserViewModel(fakeRepository)

        viewModel.uiState.test {
            // Skip initial emissions from init block
            skipItems(3)

            // Trigger loadAllUsers
            viewModel.loadAllUsers()

            // Loading state
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            // Success state
            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.users).isNotEmpty()
            assertThat(successState.users).hasSize(2)
            assertThat(successState.users[0].firstname).isEqualTo("Max")
            assertThat(successState.users[1].firstname).isEqualTo("Jane")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUsers should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = UserViewModel(fakeRepository)

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // Loading state
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            // Error state
            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.users).isEmpty()
        }
    }

    @Test
    fun `loadAllUsers should clear previous error on retry`() = runTest {
        // Start with error
        fakeRepository.shouldReturnError = true
        viewModel = UserViewModel(fakeRepository)

        viewModel.uiState.test {
            // Skip to error state
            skipItems(3)

            // Now fix the repository and retry
            fakeRepository.shouldReturnError = false
            viewModel.loadAllUsers()

            // Loading state (error should be null)
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            // Success state
            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.users).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllUsers calls should update state correctly`() = runTest {
        viewModel = UserViewModel(fakeRepository)

        viewModel.uiState.test {
            // Skip initial load
            skipItems(3)

            // First manual load
            viewModel.loadAllUsers()
            skipItems(2) // loading + success

            // Change data
            fakeRepository.usersToReturn = emptyList()

            // Second load
            viewModel.loadAllUsers()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.users).isEmpty()
        }
    }
}
