package com.app.quizapp.presentation.settings

import app.cash.turbine.test
import com.app.quizapp.data.repository.fake.FakeUserRepository
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.security.FakeTokenManager
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
 * Unit tests for SettingsViewModel
 * Tests admin detection and logout functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var tokenManager: FakeTokenManager
    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val adminUser = User(
        userId = 1,
        surname = "Admin",
        firstname = "Super",
        birthdate = "1990-01-01",
        nickname = null,
        email = "admin@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_ADMIN")
    )

    private val regularUser = User(
        userId = 2,
        surname = "User",
        firstname = "Regular",
        birthdate = "2000-01-01",
        nickname = "regular",
        email = "user@test.com",
        password = "password123",
        userRole = UserRole(2, "ROLE_USER")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = FakeUserRepository()
        tokenManager = FakeTokenManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        userRepository.clearTestData()
        tokenManager.reset()
    }

    // ========== Init Tests ==========

    @Test
    fun `init with admin user sets isAdmin true`() = runTest {
        // Given: Admin user
        userRepository.currentUser = adminUser

        // When: ViewModel is created
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // Then: Should set isAdmin to true
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.shouldNavigateToWelcome).isFalse()
        }
    }

    @Test
    fun `init with regular user sets isAdmin false`() = runTest {
        // Given: Regular user
        userRepository.currentUser = regularUser

        // When: ViewModel is created
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // Then: Should set isAdmin to false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isFalse()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init with error sets isAdmin false`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true

        // When: ViewModel is created
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // Then: Should set isAdmin to false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isFalse()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `admin check is case sensitive - lowercase admin not detected`() = runTest {
        // Given: User with "admin" role (not "ROLE_ADMIN")
        val lowerCaseAdminUser = adminUser.copy(
            userRole = UserRole(3, "admin")
        )
        userRepository.currentUser = lowerCaseAdminUser

        // When: ViewModel is created
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // Then: Should NOT detect as admin (requires "ROLE_ADMIN")
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isAdmin).isFalse()
        }
    }

    // ========== Logout Tests ==========

    @Test
    fun `logout clears token and sets navigation flag`() = runTest {
        // Given: User logged in with token
        tokenManager.saveToken("test-token")
        userRepository.currentUser = regularUser
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // When: Logout is called
        viewModel.logoutUser()
        advanceUntilIdle()

        // Then: Should clear token and set navigation flag
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.shouldNavigateToWelcome).isTrue()
        }
        assertThat(tokenManager.hasToken()).isFalse()
    }

    @Test
    fun `logout can be called directly as suspend function`() = runTest {
        // Given: User logged in
        tokenManager.saveToken("test-token")
        userRepository.currentUser = regularUser
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // When: Logout is called as suspend function
        viewModel.logout()

        // Then: Should clear token and set navigation flag
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.shouldNavigateToWelcome).isTrue()
        }
        assertThat(tokenManager.hasToken()).isFalse()
    }

    // ========== Navigation Handling Tests ==========

    @Test
    fun `onNavigationHandled resets navigation flag`() = runTest {
        // Given: User logged out
        tokenManager.saveToken("test-token")
        userRepository.currentUser = regularUser
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        viewModel.logoutUser()
        advanceUntilIdle()

        // When: Navigation is handled
        viewModel.onNavigationHandled()

        // Then: Should reset navigation flag
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.shouldNavigateToWelcome).isFalse()
        }
    }

    @Test
    fun `onNavigationHandled can be called multiple times`() = runTest {
        // Given: ViewModel initialized
        userRepository.currentUser = regularUser
        viewModel = SettingsViewModel(userRepository, tokenManager)
        advanceUntilIdle()

        // When: onNavigationHandled called multiple times
        viewModel.onNavigationHandled()
        viewModel.onNavigationHandled()
        viewModel.onNavigationHandled()

        // Then: Should not cause errors
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.shouldNavigateToWelcome).isFalse()
        }
    }
}
