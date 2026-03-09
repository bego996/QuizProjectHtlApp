package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for UserQuestion (quiz attempt) operations
 * Admin CRUD operations for managing quiz attempts
 */
interface UserQuestionRepository {

    /**
     * Get all user questions/quiz attempts (admin only, max 10)
     * @return List of quiz attempts
     */
    suspend fun getAllUserQuestions(): Result<List<UserQuestion>>

    /**
     * Get user question by ID (admin only)
     * @param userQuestionId UserQuestion ID
     * @return Quiz attempt
     */
    suspend fun getUserQuestionById(userQuestionId: Int): Result<UserQuestion>

    /**
     * Create new user question (admin only)
     * @param userQuestion UserQuestion entity
     * @return Created user question
     */
    suspend fun createUserQuestion(userQuestion: UserQuestion): Result<UserQuestion>

    /**
     * Update user question (admin only)
     * @param userQuestion UserQuestion entity with updates
     * @return Updated user question
     */
    suspend fun updateUserQuestion(userQuestion: UserQuestion): Result<UserQuestion>

    /**
     * Delete user question (admin only)
     * @param userQuestionId UserQuestion ID to delete
     * @return Deleted user question
     */
    suspend fun deleteUserQuestion(userQuestionId: Int): Result<UserQuestion>

    /**
     * Delete all user questions for a specific user (admin only)
     * Resets all quiz attempts/progress for the user
     * @param userId User ID whose quiz attempts should be deleted
     * @return Success/Error result
     */
    suspend fun deleteUserQuestionsByUserId(userId: Int): Result<Unit>
}
