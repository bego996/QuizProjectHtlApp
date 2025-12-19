package com.app.quizapp.presentation.admin

import app.cash.turbine.test
import com.app.quizapp.data.repository.fake.FakeQuestionRepository
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.model.Topic
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
 * Unit tests for ActiveQuizViewModel
 * Tests loading quizzes and delete functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ActiveQuizViewModelTest {

    private lateinit var questionRepository: FakeQuestionRepository
    private lateinit var viewModel: ActiveQuizViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testTopic = Topic(1, "Mathematics", null)
    private val testStatus = Status(1, "Active")
    private val testDifficulty = Difficulty(1, "Easy")

    private val question1 = Question(
        questionId = 1,
        questionText = "What is 2+2?",
        reviewedBy = 0,
        topic = testTopic,
        status = testStatus,
        difficulty = testDifficulty
    )

    private val question2 = Question(
        questionId = 2,
        questionText = "What is 5*5?",
        reviewedBy = 0,
        topic = testTopic,
        status = testStatus,
        difficulty = testDifficulty
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = FakeQuestionRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        questionRepository.clearTestData()
    }

    // ========== Init Tests ==========

    @Test
    fun `init loads quizzes successfully`() = runTest {
        // Given: Quizzes exist
        questionRepository.addTestQuestion(question1)
        questionRepository.addTestQuestion(question2)

        // When: ViewModel is created
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // Then: Should load all quizzes
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.quizzes).hasSize(2)
            assertThat(state.quizzes).contains(question1)
            assertThat(state.quizzes).contains(question2)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `init with no quizzes returns empty list`() = runTest {
        // Given: No quizzes exist

        // When: ViewModel is created
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // Then: Should return empty list
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.quizzes).isEmpty()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init with error sets error message`() = runTest {
        // Given: Repository returns error
        questionRepository.shouldReturnError = true
        questionRepository.errorMessage = "Network error"

        // When: ViewModel is created
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // Then: Should set error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            assertThat(state.quizzes).isEmpty()
        }
    }

    // ========== Delete Quiz Tests ==========

    @Test
    fun `deleteQuiz removes quiz and reloads list`() = runTest {
        // Given: Quizzes loaded
        questionRepository.addTestQuestion(question1)
        questionRepository.addTestQuestion(question2)
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // When: Quiz is deleted
        viewModel.deleteQuiz(1)
        advanceUntilIdle()

        // Then: Should reload list without deleted quiz
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.quizzes).hasSize(1)
            assertThat(state.quizzes).doesNotContain(question1)
            assertThat(state.quizzes).contains(question2)
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `deleteQuiz with invalid id shows error`() = runTest {
        // Given: Quizzes loaded
        questionRepository.addTestQuestion(question1)
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // When: Delete with invalid ID
        viewModel.deleteQuiz(999)
        advanceUntilIdle()

        // Then: Should show error
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Failed to delete quiz")
            assertThat(state.isLoading).isFalse()
        }
    }

    // ========== Refresh Tests ==========

    @Test
    fun `refresh reloads quizzes`() = runTest {
        // Given: Initial quizzes loaded
        questionRepository.addTestQuestion(question1)
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // When: New quiz added and refresh is called
        questionRepository.addTestQuestion(question2)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should reload with new quiz
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.quizzes).hasSize(2)
        }
    }

    @Test
    fun `refresh clears previous error`() = runTest {
        // Given: Initial load with error
        questionRepository.shouldReturnError = true
        questionRepository.errorMessage = "Network error"
        viewModel = ActiveQuizViewModel(questionRepository)
        advanceUntilIdle()

        // When: Error is fixed and refresh is called
        questionRepository.shouldReturnError = false
        questionRepository.addTestQuestion(question1)
        viewModel.refresh()
        advanceUntilIdle()

        // Then: Should clear error and load quizzes
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            assertThat(state.quizzes).isNotEmpty()
        }
    }
}
