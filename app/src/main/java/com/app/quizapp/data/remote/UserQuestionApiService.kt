package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.UserQuestionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for UserQuestion (quiz attempt) management
 * All endpoints require ROLE_admin
 * Note: User quiz endpoints (start, submit) are in UserApiService
 */
interface UserQuestionApiService {

    /**
     * Get all user questions/quiz attempts (admin only, max 10)
     * GET /api/userQuestions
     * @return List of user quiz attempts
     */
    @GET("api/userQuestions")
    suspend fun getAllUserQuestions(): List<UserQuestionDto>

    /**
     * Get single user question by ID (admin only)
     * GET /api/userQuestions/{userQuestionId}
     * @param userQuestionId UserQuestion ID
     * @return Single user quiz attempt
     */
    @GET("api/userQuestions/{userQuestionId}")
    suspend fun getUserQuestionById(@Path("userQuestionId") userQuestionId: Int): UserQuestionDto

    /**
     * Create new user question (admin only)
     * POST /api/userQuestions
     * @param userQuestion UserQuestion entity
     * @return Created user question
     */
    @POST("api/userQuestions")
    suspend fun createUserQuestion(@Body userQuestion: UserQuestionDto): UserQuestionDto

    /**
     * Update user question (admin only)
     * PUT /api/userQuestions
     * @param userQuestion UserQuestion entity
     * @return Updated user question
     */
    @PUT("api/userQuestions")
    suspend fun updateUserQuestion(@Body userQuestion: UserQuestionDto): UserQuestionDto

    /**
     * Delete user question by ID (admin only)
     * DELETE /api/userQuestions/{userQuestionId}
     * @param userQuestionId UserQuestion ID to delete
     * @return Deleted user question
     */
    @DELETE("api/userQuestions/{userQuestionId}")
    suspend fun deleteUserQuestion(@Path("userQuestionId") userQuestionId: Int): UserQuestionDto
}
