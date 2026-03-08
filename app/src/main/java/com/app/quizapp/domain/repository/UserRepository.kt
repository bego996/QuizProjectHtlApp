package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for user-related operations
 * Includes admin CRUD, user profile, and quiz operations
 */
interface UserRepository {

    // ============================================
    // Admin Operations
    // ============================================

    /**
     * Get all users (admin only, max 10)
     * @return List of users
     */
    suspend fun getAllUsers(): Result<List<User>>

    /**
     * Get user by ID (admin only)
     * @param userId User ID
     * @return User entity
     */
    suspend fun getUserById(userId: Int): Result<User>

    /**
     * Create new user (admin only)
     * @param user User entity to create
     * @return Created user
     */
    suspend fun createUser(user: User): Result<User>

    /**
     * Update user (admin only)
     * @param user User entity with updates
     * @return Updated user
     */
    suspend fun updateUser(user: User): Result<User>

    /**
     * Delete user (admin only)
     * @param userId User ID to delete
     * @return Deleted user
     */
    suspend fun deleteUser(userId: Int): Result<User>

    // ============================================
    // User Profile Operations
    // ============================================

    /**
     * Get current user's profile
     * @return Current user entity
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * Update current user's profile (without password/role)
     * @param firstname First name
     * @param surname Surname
     * @param nickname Optional nickname
     * @param birthdate Birthdate (format: yyyy-MM-dd)
     * @return Updated user
     */
    suspend fun updateCurrentUser(
        firstname: String,
        surname: String,
        nickname: String?,
        birthdate: String
    ): Result<User>

    /**
     * Change current user's password
     * @param currentPassword Current password
     * @param newPassword New password
     * @return Success message
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String>

    // ============================================
    // Quiz Operations
    // ============================================

    /**
     * Get all quiz attempts for current user
     * @return List of quiz attempts
     */
    suspend fun getQuizAttempts(): Result<List<UserQuestion>>

    /**
     * Get single quiz attempt by ID for current user
     * @param userQuestionId Quiz attempt ID
     * @return Quiz attempt
     */
    suspend fun getQuizAttemptById(userQuestionId: Int): Result<UserQuestion>

    /**
     * Start a new quiz attempt
     * @param questionId Question ID to start quiz with
     * @return Created UserQuestion with score=0, answer=null
     */
    suspend fun startQuiz(questionId: Int): Result<UserQuestion>

    /**
     * Submit answer to quiz question
     * @param userQuestionId Quiz attempt ID
     * @param answerId Answer ID
     * @return Updated UserQuestion with score (100 or 0)
     */
    suspend fun submitAnswer(userQuestionId: Int, answerId: Int): Result<UserQuestion>
}
