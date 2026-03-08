package com.app.quizapp.presentation.profile

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
 * Unit tests for ProfileViewModel
 * Tests profile loading, refresh, and display name logic
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val regularUser = User(
        userId = 1,
        surname = "Doe",
        firstname = "John",
        birthdate = "2000-01-01",
        nickname = "johnny",
        email = "john@test.com",
        password = "password123",
        userRole = UserRole(1, "ROLE_user")
    )

    private val adminUser = User(
        userId = 2,
        surname = "Admin",
        firstname = "Super",
        birthdate = "1990-01-01",
        nickname = null,
        email = "admin@test.com",
        password = "password123",
        userRole = UserRole(2, "admin")
    )

    private val userWithoutNickname = User(
        userId = 3,
        surname = "Smith",
        firstname = "Jane",
        birthdate = "1995-01-01",
        nickname = null,
        email = "jane@test.com",
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

    // ========== Init Tests ==========

    @Test
    fun `init loads user successfully`() = runTest {
        // Given: User exists
        userRepository.currentUser = regularUser

        // When: ViewModel is created
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should load user
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.user).isEqualTo(regularUser)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Unauthorized"

        // When: ViewModel is created
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.user).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Unauthorized")
        }
    }

    // ========== Refresh Tests ==========

    @Test
    fun `refresh reloads user profile`() = runTest {
        // Given: Initial user loaded
        userRepository.currentUser = regularUser
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: User changes and refresh is called
        userRepository.currentUser = adminUser
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should reload with new user
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.user).isEqualTo(adminUser)
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `refresh clears previous error`() = runTest {
        // Given: Initial load with error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Network error"
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Error is fixed and refresh is called
        userRepository.shouldReturnError = false
        userRepository.currentUser = regularUser
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should clear error and load user
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            assertThat(state.user).isEqualTo(regularUser)
        }
    }

    // ========== Display Name Tests ==========

    @Test
    fun `getDisplayName with nickname returns nickname`() = runTest {
        // Given: User with nickname
        userRepository.currentUser = regularUser
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: getDisplayName is called
        val displayName = viewModel.getDisplayName()

        // Then: Should return nickname
        assertThat(displayName).isEqualTo("johnny")
    }

    @Test
    fun `getDisplayName without nickname returns full name`() = runTest {
        // Given: User without nickname but with names
        userRepository.currentUser = userWithoutNickname
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: getDisplayName is called
        val displayName = viewModel.getDisplayName()

        // Then: Should return full name
        assertThat(displayName).isEqualTo("Jane Smith")
    }

    @Test
    fun `getDisplayName with blank nickname returns full name`() = runTest {
        // Given: User with blank nickname
        val userWithBlankNickname = regularUser.copy(nickname = "   ")
        userRepository.currentUser = userWithBlankNickname
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: getDisplayName is called
        val displayName = viewModel.getDisplayName()

        // Then: Should return full name
        assertThat(displayName).isEqualTo("John Doe")
    }

    @Test
    fun `getDisplayName without nickname and names returns email`() = runTest {
        // Given: User without nickname and blank names
        val userWithOnlyEmail = regularUser.copy(
            nickname = null,
            firstname = "",
            surname = ""
        )
        userRepository.currentUser = userWithOnlyEmail
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: getDisplayName is called
        val displayName = viewModel.getDisplayName()

        // Then: Should return email
        assertThat(displayName).isEqualTo("john@test.com")
    }

    @Test
    fun `getDisplayName without user returns empty string`() = runTest {
        // Given: No user loaded (error state)
        userRepository.shouldReturnError = true
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: getDisplayName is called
        val displayName = viewModel.getDisplayName()

        // Then: Should return empty string
        assertThat(displayName).isEmpty()
    }

    // ========== Admin Check Tests ==========

    @Test
    fun `isAdmin returns true for admin user`() = runTest {
        // Given: Admin user
        userRepository.currentUser = adminUser
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: isAdmin is called
        val isAdmin = viewModel.isAdmin()

        // Then: Should return true
        assertThat(isAdmin).isTrue()
    }

    @Test
    fun `isAdmin returns false for regular user`() = runTest {
        // Given: Regular user
        userRepository.currentUser = regularUser
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: isAdmin is called
        val isAdmin = viewModel.isAdmin()

        // Then: Should return false
        assertThat(isAdmin).isFalse()
    }

    @Test
    fun `isAdmin returns false when no user loaded`() = runTest {
        // Given: No user loaded (error state)
        userRepository.shouldReturnError = true
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: isAdmin is called
        val isAdmin = viewModel.isAdmin()

        // Then: Should return false
        assertThat(isAdmin).isFalse()
    }

    @Test
    fun `isAdmin is case sensitive`() = runTest {
        // Given: User with "ROLE_admin" (not "admin")
        val userWithRoleAdmin = regularUser.copy(
            userRole = UserRole(3, "ROLE_admin")
        )
        userRepository.currentUser = userWithRoleAdmin
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: isAdmin is called
        val isAdmin = viewModel.isAdmin()

        // Then: Should return false (exact match required)
        assertThat(isAdmin).isFalse()
    }
}
