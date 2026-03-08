package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of QuestionRepository for testing
 * Provides in-memory storage and configurable error responses
 * Supports filtering by topicId, difficultyId, statusId
 */
class FakeQuestionRepository : QuestionRepository {

    // Test data storage
    private val questions = mutableListOf<Question>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllQuestions(
        topicId: Int?,
        difficultyId: Int?,
        statusId: Int?
    ): Result<List<Question>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            var filteredQuestions = questions.toList()

            // Apply filters if provided
            topicId?.let { id ->
                filteredQuestions = filteredQuestions.filter { it.topic.topicId == id }
            }
            difficultyId?.let { id ->
                filteredQuestions = filteredQuestions.filter { it.difficulty.difficultyId == id }
            }
            statusId?.let { id ->
                filteredQuestions = filteredQuestions.filter { it.status.statusId == id }
            }

            Result.Success(filteredQuestions)
        }
    }

    override suspend fun getQuestionById(questionId: Int): Result<Question> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val question = questions.find { it.questionId == questionId }
            if (question != null) {
                Result.Success(question)
            } else {
                Result.Error("Question not found with id: $questionId")
            }
        }
    }

    override suspend fun createQuestion(question: Question): Result<Question> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newQuestion = question.copy(questionId = nextId++)
            questions.add(newQuestion)
            Result.Success(newQuestion)
        }
    }

    override suspend fun updateQuestion(question: Question): Result<Question> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = questions.indexOfFirst { it.questionId == question.questionId }
            if (index != -1) {
                questions[index] = question
                Result.Success(question)
            } else {
                Result.Error("Question not found with id: ${question.questionId}")
            }
        }
    }

    override suspend fun deleteQuestion(questionId: Int): Result<Question> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val question = questions.find { it.questionId == questionId }
            if (question != null) {
                questions.remove(question)
                Result.Success(question)
            } else {
                Result.Error("Question not found with id: $questionId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test question to repository
     */
    fun addTestQuestion(question: Question) {
        questions.add(question)
        if (question.questionId >= nextId) {
            nextId = question.questionId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        questions.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of questions
     */
    fun getCount() = questions.size
}
