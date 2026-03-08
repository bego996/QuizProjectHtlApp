package com.app.quizapp.presentation.cover

import app.cash.turbine.test
import com.app.quizapp.data.repository.fake.FakeUserRepository
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.security.FakeTokenManager
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CoverViewModel
 * Tests splash screen auto-login logic and navigation
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoverViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var tokenManager: FakeTokenManager
    private lateinit var viewModel: CoverViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testUser = User(
        userId = 1,
        surname = "User",
        firstname = "Test",
        birthdate = "2000-01-01",
        nickname = "testuser",
        email = "test@example.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_user")
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

    // ========== No Token Tests ==========

    @Test
    fun `init without token navigates to welcome`() = runTest {
        // Given: No token stored
        tokenManager.reset()

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: Should navigate to welcome
        viewModel.uiState.test {
            skipItems(1) // Skip initial state (isLoading=true)
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.navigationDestination).isEqualTo("welcome")
        }
    }

    // ========== Valid Token Tests ==========

    @Test
    fun `init with valid token navigates to home`() = runTest {
        // Given: Valid token and user exists
        tokenManager.saveToken("valid-token")
        userRepository.currentUser = testUser // Set as current user

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: Should navigate to home
        viewModel.uiState.test {
            skipItems(1) // Skip initial state (isLoading=true)
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.navigationDestination).isEqualTo("home")
        }
    }

    @Test
    fun `init with valid token keeps token stored`() = runTest {
        // Given: Valid token and user exists
        tokenManager.saveToken("valid-token")
        userRepository.currentUser = testUser // Set as current user

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Wait for init to complete
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            awaitItem() // Wait for final state
        }

        // Then: Token should still be stored
        assertThat(tokenManager.hasToken()).isTrue()
        assertThat(tokenManager.getToken()).isEqualTo("valid-token")
    }

    // ========== Invalid Token Tests ==========

    @Test
    fun `init with invalid token clears token and navigates to welcome`() = runTest {
        // Given: Token exists but getCurrentUser fails
        tokenManager.saveToken("invalid-token")
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Unauthorized"

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: Should clear token and navigate to welcome
        viewModel.uiState.test {
            skipItems(1) // Skip initial state (isLoading=true)
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.navigationDestination).isEqualTo("welcome")

            // Verify token was cleared (check inside test block to ensure completion)
            assertThat(tokenManager.hasToken()).isFalse()
        }
    }

    @Test
    fun `init with expired token clears token`() = runTest {
        // Given: Token exists but backend returns error (401 Unauthorized)
        tokenManager.saveToken("expired-token")
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Token expired"

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Wait for init to complete by checking final state
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            awaitItem() // Wait for final state
        }

        // Then: Token should be cleared
        assertThat(tokenManager.hasToken()).isFalse()
        assertThat(tokenManager.getToken()).isNull()
    }

    @Test
    fun `init with network error clears token and navigates to welcome`() = runTest {
        // Given: Token exists but network error occurs
        tokenManager.saveToken("valid-token")
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Network error"

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: Should clear token and navigate to welcome
        viewModel.uiState.test {
            skipItems(1) // Skip initial state (isLoading=true)
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.navigationDestination).isEqualTo("welcome")

            // Verify token was cleared (check inside test block to ensure completion)
            assertThat(tokenManager.hasToken()).isFalse()
        }
    }

    // ========== Navigation Handling Tests ==========

    @Test
    fun `onNavigationHandled clears navigation destination`() = runTest {
        // Given: No token (navigates to welcome)
        tokenManager.reset()
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Wait for init to complete
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            awaitItem() // Wait for checkAutoLogin to complete
        }

        // When: Navigation is handled
        viewModel.onNavigationHandled()

        // Then: navigationDestination should be null
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.navigationDestination).isNull()
        }
    }

    @Test
    fun `onNavigationHandled keeps isLoading false`() = runTest {
        // Given: ViewModel initialized and auto-login completed
        tokenManager.reset()
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Wait for init to complete
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            awaitItem() // Wait for checkAutoLogin to complete
        }

        // When: Navigation is handled
        viewModel.onNavigationHandled()

        // Then: isLoading should remain false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
        }
    }

    // ========== Initial State Tests ==========

    @Test
    fun `initial state shows loading true`() = runTest {
        // Given: No token
        tokenManager.reset()

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: First emission should have isLoading=true
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            assertThat(initialState.navigationDestination).isNull()
        }
    }

    // ========== Edge Cases ==========

    @Test
    fun `multiple onNavigationHandled calls don't cause errors`() = runTest {
        // Given: ViewModel initialized
        tokenManager.reset()
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Wait for init to complete
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            awaitItem() // Wait for checkAutoLogin to complete
        }

        // When: onNavigationHandled called multiple times
        viewModel.onNavigationHandled()
        viewModel.onNavigationHandled()
        viewModel.onNavigationHandled()

        // Then: Should not crash and destination remains null
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.navigationDestination).isNull()
        }
    }

    @Test
    fun `token exists but user repository returns minimal user still navigates to home`() = runTest {
        // Given: Token exists and getCurrentUser succeeds (even with any user data)
        tokenManager.saveToken("valid-token")
        val minimalUser = User(
            userId = 999,
            surname = "",
            firstname = "",
            birthdate = "2000-01-01",
            nickname = null,
            email = "min@test.com",
            password = "",
            userRole = UserRole(1, "ROLE_user")
        )
        userRepository.currentUser = minimalUser // Set as current user

        // When: ViewModel is created
        viewModel = CoverViewModel(tokenManager, userRepository)

        // Then: Should still navigate to home (token validation passed)
        viewModel.uiState.test {
            skipItems(1) // Skip initial state (isLoading=true)
            val state = awaitItem()
            assertThat(state.navigationDestination).isEqualTo("home")
        }
    }
}
