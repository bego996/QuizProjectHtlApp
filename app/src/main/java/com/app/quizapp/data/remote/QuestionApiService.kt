package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.QuestionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for question-related endpoints
 * GET operations require authentication
 * POST, PUT, DELETE require ROLE_admin
 */
interface QuestionApiService {

    /**
     * Get all questions with optional filters
     * GET /api/questions
     * Supports filtering by topic, difficulty, and status
     * @param topicId Optional topic filter
     * @param difficultyId Optional difficulty filter
     * @param statusId Optional status filter
     * @return List of questions
     */
    @GET("api/questions")
    suspend fun getAllQuestions(
        @Query("topicId") topicId: Int? = null,
        @Query("difficultyId") difficultyId: Int? = null,
        @Query("statusId") statusId: Int? = null
    ): List<QuestionDto>

    /**
     * Get single question by ID with answers
     * GET /api/questions/{questionId}
     * @param questionId Question ID
     * @return Question with answers
     */
    @GET("api/questions/{questionId}")
    suspend fun getQuestionById(@Path("questionId") questionId: Int): QuestionDto

    /**
     * Create new question (admin only)
     * POST /api/questions
     * @param question Question entity
     * @return Created question
     */
    @POST("api/questions")
    suspend fun createQuestion(@Body question: QuestionDto): QuestionDto

    /**
     * Update existing question (admin only)
     * PUT /api/questions
     * @param question Question entity with questionId
     * @return Updated question
     */
    @PUT("api/questions")
    suspend fun updateQuestion(@Body question: QuestionDto): QuestionDto

    /**
     * Delete question by ID (admin only)
     * DELETE /api/questions/{questionId}
     * @param questionId Question ID to delete
     * @return Deleted question
     */
    @DELETE("api/questions/{questionId}")
    suspend fun deleteQuestion(@Path("questionId") questionId: Int): QuestionDto
}
