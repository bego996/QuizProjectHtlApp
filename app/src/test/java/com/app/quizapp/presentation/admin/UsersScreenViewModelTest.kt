package com.app.quizapp.presentation.admin

import app.cash.turbine.test
import com.app.quizapp.data.repository.fake.FakeUserRepository
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserRole
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
 * Unit tests for UsersScreenViewModel
 * Tests loading users and delete functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsersScreenViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: UsersScreenViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val user1 = User(
        userId = 1,
        surname = "Doe",
        firstname = "John",
        birthdate = "2000-01-01",
        nickname = "johnny",
        email = "john@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_USER")
    )

    private val user2 = User(
        userId = 2,
        surname = "Smith",
        firstname = "Jane",
        birthdate = "1995-01-01",
        nickname = "janey",
        email = "jane@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_USER")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        userRepository.clearTestData()
    }

    // ========== Init Tests ==========

    @Test
    fun `init loads all users successfully`() = runTest {
        // Given: Users exist
        userRepository.addTestUser(user1)
        userRepository.addTestUser(user2)

        // When: ViewModel is created
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should load all users
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.users).hasSize(2)
            assertThat(state.users).contains(user1)
            assertThat(state.users).contains(user2)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `init with no users returns empty list`() = runTest {
        // Given: No users exist

        // When: ViewModel is created
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should return empty list
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.users).isEmpty()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Unauthorized"

        // When: ViewModel is created
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Unauthorized")
            assertThat(state.isLoading).isFalse()
            assertThat(state.users).isEmpty()
        }
    }

    // ========== Delete User Tests ==========

    @Test
    fun `deleteUser removes user and reloads list`() = runTest {
        // Given: Users loaded
        userRepository.addTestUser(user1)
        userRepository.addTestUser(user2)
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // When: User is deleted
        viewModel.deleteUser(1)
        advanceUntilIdle()

        // Then: Should reload list without deleted user
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.users).hasSize(1)
            assertThat(state.users).doesNotContain(user1)
            assertThat(state.users).contains(user2)
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `deleteUser with invalid id shows error`() = runTest {
        // Given: Users loaded
        userRepository.addTestUser(user1)
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // When: Delete with invalid ID
        viewModel.deleteUser(999)
        advanceUntilIdle()

        // Then: Should show error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Failed to delete user")
            assertThat(state.isLoading).isFalse()
        }
    }

    // ========== Refresh Tests ==========

    @Test
    fun `refresh reloads users`() = runTest {
        // Given: Initial users loaded
        userRepository.addTestUser(user1)
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // When: New user added and refresh is called
        userRepository.addTestUser(user2)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should reload with new user
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.users).hasSize(2)
        }
    }

    @Test
    fun `refresh clears previous error`() = runTest {
        // Given: Initial load with error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Network error"
        viewModel = UsersScreenViewModel(userRepository)
        advanceUntilIdle()

        // When: Error is fixed and refresh is called
        userRepository.shouldReturnError = false
        userRepository.addTestUser(user1)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should clear error and load users
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            assertThat(state.users).isNotEmpty()
        }
    }
}
