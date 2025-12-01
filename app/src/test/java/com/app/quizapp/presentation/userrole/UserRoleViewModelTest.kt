package com.app.quizapp.presentation.userrole

import app.cash.turbine.test
import com.app.quizapp.data.repository.FakeUserRoleRepository
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
class UserRoleViewModelTest {

    private lateinit var fakeRepository: FakeUserRoleRepository
    private lateinit var viewModel: UserRoleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserRoleRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty userRoles list and not loading`() = runTest {
        viewModel = UserRoleViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userRoles).hasSize(3)
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUserRoles should emit loading state then success with userRoles`() = runTest {
        viewModel = UserRoleViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllUserRoles()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userRoles).isNotEmpty()
            assertThat(successState.userRoles).hasSize(3)
            assertThat(successState.userRoles[0].userRole).isEqualTo("Student")
            assertThat(successState.userRoles[1].userRole).isEqualTo("Teacher")
            assertThat(successState.userRoles[2].userRole).isEqualTo("Admin")
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `loadAllUserRoles should emit error state when repository returns error`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"

        viewModel = UserRoleViewModel(fakeRepository)

        viewModel.uiState.test {
            awaitItem()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo("Network error")
            assertThat(errorState.userRoles).isEmpty()
        }
    }

    @Test
    fun `loadAllUserRoles should clear previous error on retry`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = UserRoleViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            fakeRepository.shouldReturnError = false
            viewModel.loadAllUserRoles()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.error).isNull()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
            assertThat(successState.userRoles).isNotEmpty()
        }
    }

    @Test
    fun `multiple loadAllUserRoles calls should update state correctly`() = runTest {
        viewModel = UserRoleViewModel(fakeRepository)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.loadAllUserRoles()
            skipItems(2)

            fakeRepository.userRolesToReturn = emptyList()

            viewModel.loadAllUserRoles()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.userRoles).isEmpty()
        }
    }
}
