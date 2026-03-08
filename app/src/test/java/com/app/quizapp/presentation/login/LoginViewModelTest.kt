package com.app.quizapp.presentation.login

import app.cash.turbine.test
import com.app.quizapp.data.remote.dto.JwtResponseDto
import com.app.quizapp.data.repository.fake.FakeAuthRepository
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
 * Unit tests for LoginViewModel
 * Tests authentication flow, validation, and state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var tokenManager: FakeTokenManager
    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = FakeAuthRepository()
        userRepository = FakeUserRepository()
        tokenManager = FakeTokenManager()

        viewModel = LoginViewModel(authRepository, userRepository, tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        authRepository.clearTestData()
        userRepository.clearTestData()
        tokenManager.reset()
    }

    // ========== Initial State Tests ==========

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.email).isEmpty()
            assertThat(state.password).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
            assertThat(state.isLoggedIn).isFalse()
            assertThat(state.isAdmin).isFalse()
        }
    }

    // ========== Input Change Tests ==========

    @Test
    fun `onEmailChange updates email and clears error`() = runTest {
        viewModel.onEmailChange("test@example.com")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.email).isEqualTo("test@example.com")
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `onPasswordChange updates password and clears error`() = runTest {
        viewModel.onPasswordChange("password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.password).isEqualTo("password123")
            assertThat(state.error).isNull()
        }
    }

    // ========== Login Success Tests ==========

    @Test
    fun `login success with regular user sets isLoggedIn true and isAdmin false`() = runTest {
        // Given: User registered with ROLE_USER
        authRepository.addTestUser("user@test.com", "password123")
        val testUser = createTestUser(userId = 1, email = "user@test.com", role = "ROLE_USER")
        userRepository.addTestUser(testUser)
        userRepository.currentUser = testUser

        viewModel.onEmailChange("user@test.com")
        viewModel.onPasswordChange("password123")

        // When: Login
        viewModel.login()

        // Then: Check final state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoggedIn).isTrue()
            assertThat(state.isAdmin).isFalse()
            assertThat(state.error).isNull()

            // Verify token was saved
            assertThat(tokenManager.hasToken()).isTrue()
        }
    }

    @Test
    fun `login success with admin user sets isLoggedIn true and isAdmin true`() = runTest {
        // Given: User registered with ROLE_ADMIN
        authRepository.addTestUser("admin@test.com", "adminpass")
        val testAdmin = createTestUser(userId = 1, email = "admin@test.com", role = "ROLE_ADMIN")
        userRepository.addTestUser(testAdmin)
        userRepository.currentUser = testAdmin

        viewModel.onEmailChange("admin@test.com")
        viewModel.onPasswordChange("adminpass")

        // When: Login
        viewModel.login()

        // Then: Admin state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoggedIn).isTrue()
            assertThat(state.isAdmin).isTrue()
            assertThat(state.error).isNull()
        }
    }

    // ========== Login Validation Tests ==========

    @Test
    fun `login with blank email shows error`() = runTest {
        viewModel.onPasswordChange("password123")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Email is required")
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoggedIn).isFalse()
        }
    }

    @Test
    fun `login with blank password shows error`() = runTest {
        viewModel.onEmailChange("test@example.com")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Password is required")
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoggedIn).isFalse()
        }
    }

    // ========== Login Error Tests ==========

    @Test
    fun `login with invalid credentials shows error`() = runTest {
        // Given: User not registered
        viewModel.onEmailChange("wrong@test.com")
        viewModel.onPasswordChange("wrongpass")
        viewModel.login()

        // Then: Error state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoggedIn).isFalse()
            assertThat(state.error).isEqualTo("Invalid email or password")
        }
    }

    @Test
    fun `login with network error shows error message`() = runTest {
        // Given: Repository configured to return error
        authRepository.shouldReturnError = true
        authRepository.errorMessage = "Network error"

        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("password")
        viewModel.login()

        // Then: Error state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoggedIn).isFalse()
        }
    }

    @Test
    fun `login success but getCurrentUser fails clears token and shows error`() = runTest {
        // Given: Auth succeeds but user fetch fails
        authRepository.addTestUser("test@test.com", "password")
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "User fetch failed"

        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("password")
        viewModel.login()

        // Then: Error state and token cleared
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).contains("Failed to retrieve user information")
            assertThat(state.isLoggedIn).isFalse()
            assertThat(tokenManager.hasToken()).isFalse()
        }
    }

    // ========== Utility Methods Tests ==========

    @Test
    fun `clearError clears error message`() = runTest {
        // Given: Error state
        viewModel.onEmailChange("")
        viewModel.login() // This will set error
        viewModel.clearError()

        // Then: Error is null
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `resetLoginState sets isLoggedIn to false`() = runTest {
        // Given: Successful login state
        authRepository.addTestUser("test@test.com", "password")
        val testUser = createTestUser(userId = 1, email = "test@test.com", role = "ROLE_USER")
        userRepository.addTestUser(testUser)
        userRepository.currentUser = testUser

        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("password")
        viewModel.login()
        viewModel.resetLoginState()

        // Then: isLoggedIn is false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoggedIn).isFalse()
        }
    }

    // ========== Helper Functions ==========

    private fun createTestUser(
        userId: Int,
        email: String,
        role: String
    ): User {
        return User(
            userId = userId,
            email = email,
            password = "***",
            firstname = "Test",
            surname = "User",
            birthdate = "2000-01-01",
            nickname = "testuser",
            userRole = UserRole(
                userRoleId = if (role == "ROLE_ADMIN") 2 else 1,
                userRole = role
            )
        )
    }
}
