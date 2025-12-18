package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.AnswerDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for Answer CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface AnswerApiService {

    /**
     * Get all answers
     * GET answers
     * @return List of answers
     */
    @GET("answers")
    suspend fun getAllAnswers(): List<AnswerDto>

    /**
     * Get all answers by questionId
     * GET answers?questionId={questionId}
     * @param questionId Question ID
     * @return List of answers
     */
    @GET("answers")
    suspend fun getAllAnswersByQuestionId(@Query("questionId") questionId: Int): List<AnswerDto>

    /**
     * Get answer by ID
     * GET /api/answers/{answerId}
     * @param answerId Answer ID
     * @return Answer with HATEOAS links
     */
    @GET("answers/{answerId}")
    suspend fun getAnswerById(@Path("answerId") answerId: Int): AnswerDto

    /**
     * Create new answer (admin only)
     * POST /api/answers
     * @param answer Answer entity
     * @return Created answer
     */
    @POST("answers")
    suspend fun createAnswer(@Body answer: AnswerDto): AnswerDto

    /**
     * Update answer (admin only)
     * PUT /api/answers
     * @param answer Answer entity
     * @return Updated answer
     */
    @PUT("answers")
    suspend fun updateAnswer(@Body answer: AnswerDto): AnswerDto

    /**
     * Delete answer by ID (admin only)
     * DELETE /api/answers/{answerId}
     * @param answerId Answer ID to delete
     * @return Deleted answer
     */
    @DELETE("answers/{answerId}")
    suspend fun deleteAnswer(@Path("answerId") answerId: Int): AnswerDto
}