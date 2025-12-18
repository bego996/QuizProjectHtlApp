package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of AnswerRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeAnswerRepository : AnswerRepository {

    // Test data storage
    private val answers = mutableListOf<Answer>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllAnswers(): Result<List<Answer>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(answers.toList())
        }
    }

    override suspend fun getAllAnswersByQuestionId(questionId: Int): Result<List<Answer>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(answers.filter { it.question.questionId == questionId })
        }
    }

    override suspend fun getAnswerById(answerId: Int): Result<Answer> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val answer = answers.find { it.answerId == answerId }
            if (answer != null) {
                Result.Success(answer)
            } else {
                Result.Error("Answer not found with id: $answerId")
            }
        }
    }

    override suspend fun createAnswer(answer: Answer): Result<Answer> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newAnswer = answer.copy(answerId = nextId++)
            answers.add(newAnswer)
            Result.Success(newAnswer)
        }
    }

    override suspend fun updateAnswer(answer: Answer): Result<Answer> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = answers.indexOfFirst { it.answerId == answer.answerId }
            if (index != -1) {
                answers[index] = answer
                Result.Success(answer)
            } else {
                Result.Error("Answer not found with id: ${answer.answerId}")
            }
        }
    }

    override suspend fun deleteAnswer(answerId: Int): Result<Answer> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val answer = answers.find { it.answerId == answerId }
            if (answer != null) {
                answers.remove(answer)
                Result.Success(answer)
            } else {
                Result.Error("Answer not found with id: $answerId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test answer to repository
     */
    fun addTestAnswer(answer: Answer) {
        answers.add(answer)
        if (answer.answerId >= nextId) {
            nextId = answer.answerId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        answers.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of answers
     */
    fun getCount() = answers.size
}
