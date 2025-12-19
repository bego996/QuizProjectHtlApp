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
 * Unit tests for EditProfileViewModel
 * Tests profile loading, field updates, validation, and save functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: EditProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testUser = User(
        userId = 1,
        surname = "Doe",
        firstname = "John",
        birthdate = "2000-01-01",
        nickname = "johnny",
        email = "john@test.com",
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
    fun `init loads profile and prefills fields`() = runTest {
        // Given: User exists
        userRepository.currentUser = testUser

        // When: ViewModel is created
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should prefill fields with user data
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.firstname).isEqualTo("John")
            assertThat(state.surname).isEqualTo("Doe")
            assertThat(state.nickname).isEqualTo("johnny")
            assertThat(state.birthdate).isEqualTo("2000-01-01")
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
            assertThat(state.isSaved).isFalse()
        }
    }

    @Test
    fun `init with null nickname sets empty string`() = runTest {
        // Given: User without nickname
        val userWithoutNickname = testUser.copy(nickname = null)
        userRepository.currentUser = userWithoutNickname

        // When: ViewModel is created
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then: Nickname should be empty string
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.nickname).isEmpty()
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Unauthorized"

        // When: ViewModel is created
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Unauthorized")
            assertThat(state.firstname).isEmpty()
        }
    }

    // ========== Field Change Tests ==========

    @Test
    fun `onFirstnameChange updates firstname and clears error`() = runTest {
        // Given: ViewModel with loaded profile
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // Set an error first
        viewModel.updateProfile() // Will fail validation, but we just want error
        advanceUntilIdle()

        // When: Firstname is changed
        viewModel.onFirstnameChange("Jane")

        // Then: Should update firstname and clear error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.firstname).isEqualTo("Jane")
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `onSurnameChange updates surname and clears error`() = runTest {
        // Given: ViewModel initialized
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Surname is changed
        viewModel.onSurnameChange("Smith")

        // Then: Should update surname
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.surname).isEqualTo("Smith")
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `onNicknameChange updates nickname and clears error`() = runTest {
        // Given: ViewModel initialized
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Nickname is changed
        viewModel.onNicknameChange("janey")

        // Then: Should update nickname
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.nickname).isEqualTo("janey")
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `onBirthdateChange updates birthdate and clears error`() = runTest {
        // Given: ViewModel initialized
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Birthdate is changed
        viewModel.onBirthdateChange("1995-05-15")

        // Then: Should update birthdate
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.birthdate).isEqualTo("1995-05-15")
            assertThat(state.error).isNull()
        }
    }

    // ========== Update Profile Validation Tests ==========

    @Test
    fun `updateProfile validates firstname is required`() = runTest {
        // Given: Profile loaded with valid data
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Firstname is cleared and update is called
        viewModel.onFirstnameChange("")
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should show validation error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("First name is required")
            assertThat(state.isSaved).isFalse()
        }
    }

    @Test
    fun `updateProfile validates surname is required`() = runTest {
        // Given: Profile loaded
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Surname is cleared and update is called
        viewModel.onSurnameChange("")
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should show validation error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Surname is required")
            assertThat(state.isSaved).isFalse()
        }
    }

    @Test
    fun `updateProfile validates birthdate is required`() = runTest {
        // Given: Profile loaded
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Birthdate is cleared and update is called
        viewModel.onBirthdateChange("")
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should show validation error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Birthdate is required")
            assertThat(state.isSaved).isFalse()
        }
    }

    // ========== Update Profile Success Tests ==========

    @Test
    fun `updateProfile success sets isSaved true`() = runTest {
        // Given: Profile loaded
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Profile is updated with valid data
        viewModel.onFirstnameChange("Jane")
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should set isSaved to true
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isSaved).isTrue()
            assertThat(state.error).isNull()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `updateProfile converts blank nickname to null`() = runTest {
        // Given: Profile loaded
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        // When: Nickname is set to empty and profile is updated
        viewModel.onNicknameChange("")
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should save successfully (nickname converted to null)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isSaved).isTrue()
        }

        // Verify repository received null for nickname
        assertThat(userRepository.currentUser?.nickname).isNull()
    }

    // ========== Update Profile Error Tests ==========

    @Test
    fun `updateProfile with backend error shows error message`() = runTest {
        // Given: Profile loaded but update will fail
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        userRepository.shouldReturnError = true
        userRepository.errorMessage = "Server error"

        // When: Profile update is attempted
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then: Should show error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Server error")
            assertThat(state.isSaved).isFalse()
            assertThat(state.isLoading).isFalse()
        }
    }

    // ========== Reset Saved State Tests ==========

    @Test
    fun `resetSavedState sets isSaved to false`() = runTest {
        // Given: Profile saved successfully
        userRepository.currentUser = testUser
        viewModel = EditProfileViewModel(userRepository)
        advanceUntilIdle()

        viewModel.updateProfile()
        advanceUntilIdle()

        // When: Saved state is reset
        viewModel.resetSavedState()

        // Then: isSaved should be false
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isSaved).isFalse()
        }
    }
}
