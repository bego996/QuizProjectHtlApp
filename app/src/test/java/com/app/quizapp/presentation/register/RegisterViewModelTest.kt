package com.app.quizapp.presentation.register

import app.cash.turbine.test
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
 * Unit tests for RegisterViewModel
 * Tests registration flow, validation, and state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var tokenManager: FakeTokenManager
    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = FakeAuthRepository()
        userRepository = FakeUserRepository()
        tokenManager = FakeTokenManager()

        viewModel = RegisterViewModel(authRepository, userRepository, tokenManager)
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
            assertThat(state.firstname).isEmpty()
            assertThat(state.surname).isEmpty()
            assertThat(state.birthdate).isEmpty()
            assertThat(state.nickname).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
            assertThat(state.isRegistered).isFalse()
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
    fun `onPasswordChange updates password`() = runTest {
        viewModel.onPasswordChange("password123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.password).isEqualTo("password123")
        }
    }

    @Test
    fun `onFirstnameChange updates firstname`() = runTest {
        viewModel.onFirstnameChange("John")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.firstname).isEqualTo("John")
        }
    }

    // ========== Registration Success Tests ==========

    @Test
    fun `register success with valid data sets isRegistered true`() = runTest {
        // Given: Valid registration data and mock user response
        val testUser = createTestUser(userId = 1, email = "newuser@test.com", role = "ROLE_USER")
        userRepository.addTestUser(testUser)
        userRepository.currentUser = testUser

        viewModel.onEmailChange("newuser@test.com")
        viewModel.onPasswordChange("password123")
        viewModel.onFirstnameChange("John")
        viewModel.onSurnameChange("Doe")
        viewModel.onBirthdateChange("01.01.2000")
        viewModel.onNicknameChange("johnd")

        // When: Register
        viewModel.register()

        // Then: Success state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isRegistered).isTrue()
            assertThat(state.isAdmin).isFalse()
            assertThat(state.error).isNull()

            // Verify token was saved (auto-login)
            assertThat(tokenManager.hasToken()).isTrue()
        }
    }

    @Test
    fun `register success with admin role sets isAdmin true`() = runTest {
        // Given: Admin user
        val testAdmin = createTestUser(userId = 1, email = "admin@test.com", role = "admin")
        userRepository.addTestUser(testAdmin)
        userRepository.currentUser = testAdmin

        viewModel.onEmailChange("admin@test.com")
        viewModel.onPasswordChange("adminpass123")
        viewModel.onFirstnameChange("Admin")
        viewModel.onSurnameChange("User")
        viewModel.onBirthdateChange("01.01.1990")

        // When: Register
        viewModel.register()

        // Then: Admin state
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isRegistered).isTrue()
            assertThat(state.isAdmin).isTrue()
        }
    }

    // ========== Validation Error Tests ==========

    @Test
    fun `register with blank email shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onEmailChange("")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Email is required")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with blank password shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onPasswordChange("")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Password is required")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with password less than 8 characters shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onPasswordChange("pass123") // Only 7 chars
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Password must be at least 8 characters")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with blank firstname shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onFirstnameChange("")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("First name is required")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with blank surname shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onSurnameChange("")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Surname is required")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with blank birthdate shows error`() = runTest {
        fillValidRegistrationData()
        viewModel.onBirthdateChange("")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Birthdate is required")
            assertThat(state.isRegistered).isFalse()
        }
    }

    // ========== Registration Error Tests ==========

    @Test
    fun `register with existing email shows error`() = runTest {
        // Given: Email already exists
        authRepository.addTestUser("existing@test.com", "somepassword")
        fillValidRegistrationData()
        viewModel.onEmailChange("existing@test.com")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Email already exists")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register with network error shows error message`() = runTest {
        // Given: Repository configured to return error
        authRepository.shouldReturnError = true
        authRepository.errorMessage = "Network connection failed"
        fillValidRegistrationData()
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network connection failed")
            assertThat(state.isRegistered).isFalse()
        }
    }

    @Test
    fun `register success but getCurrentUser fails clears token and shows error`() = runTest {
        // Given: Register succeeds but user fetch fails
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Failed to fetch user"
        fillValidRegistrationData()
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).contains("Failed to retrieve user information")
            assertThat(state.isRegistered).isFalse()
            assertThat(tokenManager.hasToken()).isFalse()
        }
    }

    // ========== Utility Methods Tests ==========

    @Test
    fun `clearError clears error message`() = runTest {
        viewModel.register() // Will set "Email is required" error
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `resetRegisterState sets isRegistered to false`() = runTest {
        // Given: Successful registration
        val testUser = createTestUser(userId = 1, email = "test@test.com", role = "ROLE_USER")
        userRepository.addTestUser(testUser)
        userRepository.currentUser = testUser
        fillValidRegistrationData()
        viewModel.register()
        viewModel.resetRegisterState()

        // Then: isRegistered is false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isRegistered).isFalse()
        }
    }

    // ========== Helper Functions ==========

    private fun fillValidRegistrationData() {
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("password123")
        viewModel.onFirstnameChange("John")
        viewModel.onSurnameChange("Doe")
        viewModel.onBirthdateChange("01.01.2000")
        viewModel.onNicknameChange("johnd")
    }

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
                userRoleId = if (role == "admin") 2 else 1,
                userRole = role
            )
        )
    }
}
