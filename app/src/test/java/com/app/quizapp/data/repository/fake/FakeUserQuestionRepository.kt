package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserQuestionRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeUserQuestionRepository : UserQuestionRepository {

    // Test data storage
    private val userQuestions = mutableListOf<UserQuestion>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllUserQuestions(): Result<List<UserQuestion>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(userQuestions.toList())
        }
    }

    override suspend fun getUserQuestionById(userQuestionId: Int): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userQuestion = userQuestions.find { it.userQuestionId == userQuestionId }
            if (userQuestion != null) {
                Result.Success(userQuestion)
            } else {
                Result.Error("UserQuestion not found with id: $userQuestionId")
            }
        }
    }

    override suspend fun createUserQuestion(userQuestion: UserQuestion): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newUserQuestion = userQuestion.copy(userQuestionId = nextId++)
            userQuestions.add(newUserQuestion)
            Result.Success(newUserQuestion)
        }
    }

    override suspend fun updateUserQuestion(userQuestion: UserQuestion): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = userQuestions.indexOfFirst { it.userQuestionId == userQuestion.userQuestionId }
            if (index != -1) {
                userQuestions[index] = userQuestion
                Result.Success(userQuestion)
            } else {
                Result.Error("UserQuestion not found with id: ${userQuestion.userQuestionId}")
            }
        }
    }

    override suspend fun deleteUserQuestion(userQuestionId: Int): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userQuestion = userQuestions.find { it.userQuestionId == userQuestionId }
            if (userQuestion != null) {
                userQuestions.remove(userQuestion)
                Result.Success(userQuestion)
            } else {
                Result.Error("UserQuestion not found with id: $userQuestionId")
            }
        }
    }

    override suspend fun deleteUserQuestionsByUserId(userId: Int): Result<Unit> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            userQuestions.removeAll { it.user.userId == userId }
            Result.Success(Unit)
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test user question to repository
     */
    fun addTestUserQuestion(userQuestion: UserQuestion) {
        userQuestions.add(userQuestion)
        if (userQuestion.userQuestionId >= nextId) {
            nextId = userQuestion.userQuestionId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        userQuestions.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of user questions
     */
    fun getCount() = userQuestions.size
}
