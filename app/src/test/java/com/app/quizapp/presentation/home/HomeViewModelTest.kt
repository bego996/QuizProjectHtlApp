package com.app.quizapp.presentation.home

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
 * Unit tests for HomeViewModel
 * Tests user info loading, admin detection, and refresh functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val regularUser = User(
        userId = 1,
        surname = "User",
        firstname = "Test",
        birthdate = "2000-01-01",
        nickname = "testuser",
        email = "user@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_user")
    )

    private val adminUser = User(
        userId = 2,
        surname = "Admin",
        firstname = "Test",
        birthdate = "1990-01-01",
        nickname = "testadmin",
        email = "admin@test.com",
        password = "password123",
        userRole = UserRole(2, "admin")
    )

    private val userWithoutNickname = User(
        userId = 3,
        surname = "NoNick",
        firstname = "Test",
        birthdate = "1995-01-01",
        nickname = null,
        email = "nonick@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_user")
    )

    private val userWithEmptyNickname = User(
        userId = 4,
        surname = "Empty",
        firstname = "Test",
        birthdate = "1995-01-01",
        nickname = "",
        email = "empty@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_user")
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

    // ========== Initial State Tests ==========

    @Test
    fun `init with error shows error state`() = runTest {
        // Given: Repository will error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Test error"

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should show error state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Test error")
            assertThat(state.userName).isEmpty()
            assertThat(state.isAdmin).isFalse()
        }
    }

    // ========== User Info Loading Success Tests ==========

    @Test
    fun `loadUserInfo success with nickname shows nickname as userName`() = runTest {
        // Given: User with nickname
        userRepository.currentUser = regularUser

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should show nickname as userName
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("testuser")
            assertThat(state.isAdmin).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `loadUserInfo success without nickname shows email as userName`() = runTest {
        // Given: User without nickname
        userRepository.currentUser = userWithoutNickname

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should show email as userName
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("nonick@test.com")
            assertThat(state.isAdmin).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `loadUserInfo success with empty nickname shows email as userName`() = runTest {
        // Given: User with empty nickname
        userRepository.currentUser = userWithEmptyNickname

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should show email as userName
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("empty@test.com")
            assertThat(state.isAdmin).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `loadUserInfo success with admin role sets isAdmin true`() = runTest {
        // Given: Admin user
        userRepository.currentUser = adminUser

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set isAdmin to true
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("testadmin")
            assertThat(state.isAdmin).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `loadUserInfo success with regular user role sets isAdmin false`() = runTest {
        // Given: Regular user
        userRepository.currentUser = regularUser

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set isAdmin to false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isFalse()
        }
    }

    // ========== Error Handling Tests ==========

    @Test
    fun `loadUserInfo error sets error message and stops loading`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Network error"

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set error and stop loading
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            assertThat(state.userName).isEmpty()
            assertThat(state.isAdmin).isFalse()
        }
    }

    @Test
    fun `loadUserInfo error does not set userName or isAdmin`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Unauthorized"

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: userName and isAdmin should remain default values
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEmpty()
            assertThat(state.isAdmin).isFalse()
        }
    }

    // ========== Refresh Tests ==========

    @Test
    fun `refresh reloads user info successfully`() = runTest {
        // Given: Initial user loaded
        userRepository.currentUser = regularUser
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // When: User changes and refresh is called
        userRepository.currentUser = adminUser
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should reload with new user data
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("testadmin")
            assertThat(state.isAdmin).isTrue()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `refresh clears previous error on success`() = runTest {
        // Given: Initial load with error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Network error"
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // When: Error is fixed and refresh is called
        userRepository.shouldReturnError = false
        userRepository.currentUser = regularUser
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should clear error and show user data
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            assertThat(state.userName).isEqualTo("testuser")
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `refresh can be called multiple times`() = runTest {
        // Given: User loaded
        userRepository.currentUser = regularUser
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // When: Refresh called multiple times
        viewModel.refresh()
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should complete without errors
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("testuser")
            assertThat(state.error).isNull()
        }
    }

    // ========== Edge Cases ==========

    @Test
    fun `user with blank nickname uses email`() = runTest {
        // Given: User with blank (whitespace) nickname
        val userWithBlankNickname = regularUser.copy(nickname = "   ")
        userRepository.currentUser = userWithBlankNickname

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should use email instead of blank nickname
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.userName).isEqualTo("user@test.com")
        }
    }

    @Test
    fun `admin role check is case sensitive`() = runTest {
        // Given: User with "ROLE_admin" (not "admin")
        val userWithRoleAdmin = regularUser.copy(
            userRole = UserRole(3, "ROLE_admin")
        )
        userRepository.currentUser = userWithRoleAdmin

        // When: ViewModel is created
        viewModel = HomeViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should NOT detect as admin (exact match required)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isFalse() // "ROLE_admin" != "admin"
        }
    }
}
