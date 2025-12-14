package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.ChangePasswordRequestDto
import com.app.quizapp.data.remote.dto.StartQuizRequestDto
import com.app.quizapp.data.remote.dto.SubmitAnswerRequestDto
import com.app.quizapp.data.remote.dto.UpdateProfileRequestDto
import com.app.quizapp.data.remote.dto.UserDto
import com.app.quizapp.data.remote.dto.UserQuestionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for user-related endpoints
 * Includes both admin CRUD operations and authenticated user endpoints
 */
interface UserApiService {

    // ============================================
    // Admin Endpoints - /api/users
    // Requires ROLE_admin
    // ============================================

    /**
     * Get all users (admin only, max 10 users)
     * GET /api/users
     * @return List of users with HATEOAS links
     */
    @GET("users")
    suspend fun getAllUsers(): List<UserDto>

    /**
     * Get user by ID (admin only)
     * GET /api/users/{userId}
     * @param userId User ID
     * @return User with HATEOAS links
     */
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): UserDto

    /**
     * Create new user (admin only)
     * POST /api/users
     * @param user User entity (complete)
     * @return Created user
     */
    @POST("api/users")
    suspend fun createUser(@Body user: UserDto): UserDto

    /**
     * Update existing user (admin only)
     * PUT /api/users
     * @param user User entity with userId
     * @return Updated user
     */
    @PUT("api/users")
    suspend fun updateUser(@Body user: UserDto): UserDto

    /**
     * Delete user by ID (admin only)
     * DELETE /api/users/{userId}
     * @param userId User ID to delete
     * @return Deleted user
     */
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): UserDto

    // ============================================
    // User Profile Endpoints - /api/users/me
    // Requires authentication
    // ============================================

    /**
     * Get current user's profile
     * GET /api/users/me
     * @return Current user entity
     */
    @GET("api/users/me")
    suspend fun getCurrentUser(): UserDto

    /**
     * Update current user's profile (without password/role)
     * PUT /api/users/me
     * @param updateRequest Profile update data
     * @return Updated user entity
     */
    @PUT("api/users/me")
    suspend fun updateCurrentUser(@Body updateRequest: UpdateProfileRequestDto): UserDto

    /**
     * Change current user's password
     * PUT /api/users/me/password
     * @param changePasswordRequest Current and new password
     * @return Success message
     */
    @PUT("api/users/me/password")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequestDto): String

    // ============================================
    // Quiz Attempts Endpoints - /api/users/me/quiz-attempts
    // Requires authentication
    // ============================================

    /**
     * Get all quiz attempts for current user
     * GET /api/users/me/quiz-attempts
     * @return List of user's quiz attempts
     */
    @GET("api/users/me/quiz-attempts")
    suspend fun getQuizAttempts(): List<UserQuestionDto>

    /**
     * Get single quiz attempt by ID for current user
     * GET /api/users/me/quiz-attempts/{userQuestionId}
     * @param userQuestionId Quiz attempt ID
     * @return Single quiz attempt
     */
    @GET("api/users/me/quiz-attempts/{userQuestionId}")
    suspend fun getQuizAttemptById(@Path("userQuestionId") userQuestionId: Int): UserQuestionDto

    // ============================================
    // Quiz Play Endpoints - /api/users/me/quiz
    // Requires authentication
    // ============================================

    /**
     * Start a new quiz attempt
     * POST /api/users/me/quiz/start
     * @param startRequest Question ID to start quiz with
     * @return Created UserQuestion with score=0, answer=null
     */
    @POST("api/users/me/quiz/start")
    suspend fun startQuiz(@Body startRequest: StartQuizRequestDto): UserQuestionDto

    /**
     * Submit answer to quiz question
     * PUT /api/users/me/quiz/submit/{userQuestionId}
     * @param userQuestionId Quiz attempt ID
     * @param submitRequest Answer ID
     * @return Updated UserQuestion with score (100 or 0)
     */
    @PUT("api/users/me/quiz/submit/{userQuestionId}")
    suspend fun submitAnswer(
        @Path("userQuestionId") userQuestionId: Int,
        @Body submitRequest: SubmitAnswerRequestDto
    ): UserQuestionDto
}
