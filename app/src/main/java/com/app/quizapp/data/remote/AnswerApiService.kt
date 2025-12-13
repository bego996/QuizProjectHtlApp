package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.AnswerDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for Answer CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface AnswerApiService {

    /**
     * Get all answers (max 10 with HATEOAS links)
     * GET /api/answers
     * @return List of answers
     */
    @GET("api/answers")
    suspend fun getAllAnswers(): List<AnswerDto>

    /**
     * Get answer by ID
     * GET /api/answers/{answerId}
     * @param answerId Answer ID
     * @return Answer with HATEOAS links
     */
    @GET("api/answers/{answerId}")
    suspend fun getAnswerById(@Path("answerId") answerId: Int): AnswerDto

    /**
     * Create new answer (admin only)
     * POST /api/answers
     * @param answer Answer entity
     * @return Created answer
     */
    @POST("api/answers")
    suspend fun createAnswer(@Body answer: AnswerDto): AnswerDto

    /**
     * Update answer (admin only)
     * PUT /api/answers
     * @param answer Answer entity
     * @return Updated answer
     */
    @PUT("api/answers")
    suspend fun updateAnswer(@Body answer: AnswerDto): AnswerDto

    /**
     * Delete answer by ID (admin only)
     * DELETE /api/answers/{answerId}
     * @param answerId Answer ID to delete
     * @return Deleted answer
     */
    @DELETE("api/answers/{answerId}")
    suspend fun deleteAnswer(@Path("answerId") answerId: Int): AnswerDto
}