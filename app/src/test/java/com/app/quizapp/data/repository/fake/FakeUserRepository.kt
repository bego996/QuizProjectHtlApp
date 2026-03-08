package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserRepository for testing
 * Provides in-memory storage and configurable error responses
 * Supports both admin CRUD and user profile operations
 */
class FakeUserRepository : UserRepository {

    // Test data storage
    private val users = mutableListOf<User>()
    private val quizAttempts = mutableListOf<UserQuestion>()
    private var nextUserId = 1
    private var nextQuizAttemptId = 1
    private val testQuestions = mutableMapOf<Int, com.app.quizapp.domain.model.Question>() // questionId -> Question

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"
    var currentUser: User? = null  // Simulates logged-in user
    var simulateDelay = false  // For testing async behavior
    var shouldFailStartQuiz = false  // Fail startQuiz calls
    var shouldFailSubmitAnswer = false  // Fail submitAnswer calls

    // ========== Admin CRUD Operations ==========

    override suspend fun getAllUsers(): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(users.toList())
        }
    }

    override suspend fun getUserById(userId: Int): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val user = users.find { it.userId == userId }
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error("User not found with id: $userId")
            }
        }
    }

    override suspend fun createUser(user: User): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newUser = user.copy(userId = nextUserId++)
            users.add(newUser)
            Result.Success(newUser)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = users.indexOfFirst { it.userId == user.userId }
            if (index != -1) {
                users[index] = user
                Result.Success(user)
            } else {
                Result.Error("User not found with id: ${user.userId}")
            }
        }
    }

    override suspend fun deleteUser(userId: Int): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val user = users.find { it.userId == userId }
            if (user != null) {
                users.remove(user)
                Result.Success(user)
            } else {
                Result.Error("User not found with id: $userId")
            }
        }
    }

    // ========== User Profile Operations ==========

    override suspend fun getCurrentUser(): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                Result.Success(currentUser!!)
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    override suspend fun updateCurrentUser(
        firstname: String,
        surname: String,
        nickname: String?,
        birthdate: String
    ): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                val updatedUser = currentUser!!.copy(
                    firstname = firstname,
                    surname = surname,
                    nickname = nickname,
                    birthdate = birthdate
                )
                currentUser = updatedUser
                // Update in users list too
                val index = users.indexOfFirst { it.userId == updatedUser.userId }
                if (index != -1) {
                    users[index] = updatedUser
                }
                Result.Success(updatedUser)
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                if (currentUser!!.password == currentPassword) {
                    val updatedUser = currentUser!!.copy(password = newPassword)
                    currentUser = updatedUser
                    // Update in users list too
                    val index = users.indexOfFirst { it.userId == updatedUser.userId }
                    if (index != -1) {
                        users[index] = updatedUser
                    }
                    Result.Success("Password changed successfully")
                } else {
                    Result.Error("Current password is incorrect")
                }
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    // ========== Quiz Operations ==========

    override suspend fun getQuizAttempts(): Result<List<UserQuestion>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                Result.Success(quizAttempts.filter { it.user.userId == currentUser!!.userId })
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    override suspend fun getQuizAttemptById(userQuestionId: Int): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                val attempt = quizAttempts.find {
                    it.userQuestionId == userQuestionId && it.user.userId == currentUser!!.userId
                }
                if (attempt != null) {
                    Result.Success(attempt)
                } else {
                    Result.Error("Quiz attempt not found")
                }
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    override suspend fun startQuiz(questionId: Int): Result<UserQuestion> {
        // Simulate delay if configured
        if (simulateDelay) {
            kotlinx.coroutines.delay(100)
        }

        return if (shouldReturnError || shouldFailStartQuiz) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                val question = testQuestions[questionId]
                if (question != null) {
                    val userQuestion = UserQuestion(
                        userQuestionId = nextQuizAttemptId++,
                        user = currentUser!!,
                        question = question,
                        score = 0  // Initial score before answer submission
                    )
                    quizAttempts.add(userQuestion)
                    Result.Success(userQuestion)
                } else {
                    Result.Error("Question not found with id: $questionId")
                }
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    override suspend fun submitAnswer(userQuestionId: Int, answerId: Int): Result<UserQuestion> {
        // Simulate delay if configured
        if (simulateDelay) {
            kotlinx.coroutines.delay(100)
        }

        return if (shouldReturnError || shouldFailSubmitAnswer) {
            Result.Error(errorMessage)
        } else {
            if (currentUser != null) {
                val attempt = quizAttempts.find {
                    it.userQuestionId == userQuestionId && it.user.userId == currentUser!!.userId
                }
                if (attempt != null) {
                    // Update score (simplified - would check if answer is correct)
                    val updatedAttempt = attempt.copy(score = 100)
                    val index = quizAttempts.indexOf(attempt)
                    quizAttempts[index] = updatedAttempt
                    Result.Success(updatedAttempt)
                } else {
                    Result.Error("Quiz attempt not found")
                }
            } else {
                Result.Error("No user logged in")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test user to repository
     */
    fun addTestUser(user: User) {
        users.add(user)
        if (user.userId >= nextUserId) {
            nextUserId = user.userId + 1
        }
    }

    /**
     * Add test quiz attempt
     */
    fun addTestQuizAttempt(userQuestion: UserQuestion) {
        quizAttempts.add(userQuestion)
        if (userQuestion.userQuestionId >= nextQuizAttemptId) {
            nextQuizAttemptId = userQuestion.userQuestionId + 1
        }
    }

    /**
     * Add test question for startQuiz functionality
     */
    fun addTestQuestion(question: com.app.quizapp.domain.model.Question) {
        testQuestions[question.questionId] = question
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        users.clear()
        quizAttempts.clear()
        testQuestions.clear()
        nextUserId = 1
        nextQuizAttemptId = 1
        shouldReturnError = false
        shouldFailStartQuiz = false
        shouldFailSubmitAnswer = false
        simulateDelay = false
        currentUser = null
    }

    /**
     * Get count of users
     */
    fun getCount() = users.size
}
