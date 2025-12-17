package com.app.quizapp.domain.repository

import com.app.quizapp.data.remote.dto.AnswerDto
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.util.Result
import retrofit2.http.Path

/**
 * Repository interface for Answer operations
 * Provides CRUD operations for answer management
 */
interface AnswerRepository {

    /**
     * Get all answers
     * @return List of answers
     */
    suspend fun getAllAnswers(): Result<List<Answer>>

    /**
     * Get all answers by questionId
     * @param questionId Question Id
     * @return List of answers
     */
    suspend fun getAllAnswersByQuestionId(@Path ("questionId") questionId: Int ): Result<List<Answer>>

    /**
     * Get answer by ID
     * @param answerId Answer ID
     * @return Answer entity
     */
    suspend fun getAnswerById(answerId: Int): Result<Answer>

    /**
     * Create new answer (admin only)
     * @param answer Answer entity
     * @return Created answer
     */
    suspend fun createAnswer(answer: Answer): Result<Answer>

    /**
     * Update answer (admin only)
     * @param answer Answer entity with updates
     * @return Updated answer
     */
    suspend fun updateAnswer(answer: Answer): Result<Answer>

    /**
     * Delete answer (admin only)
     * @param answerId Answer ID to delete
     * @return Deleted answer
     */
    suspend fun deleteAnswer(answerId: Int): Result<Answer>
}