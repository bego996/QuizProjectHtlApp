package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.DifficultyDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for Difficulty CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface DifficultyApiService {

    /**
     * Get all difficulties (max 10 with HATEOAS links)
     * GET /api/difficulties
     * @return List of difficulties
     */
    @GET("difficulties")
    suspend fun getAllDifficulties(): List<DifficultyDto>

    /**
     * Get difficulty by ID
     * GET /api/difficulties/{difficultyId}
     * @param difficultyId Difficulty ID
     * @return Difficulty with HATEOAS links
     */
    @GET("difficulties/{difficultyId}")
    suspend fun getDifficultyById(@Path("difficultyId") difficultyId: Int): DifficultyDto

    /**
     * Create new difficulty (admin only)
     * POST /api/difficulties
     * @param difficulty Difficulty entity
     * @return Created difficulty
     */
    @POST("difficulties")
    suspend fun createDifficulty(@Body difficulty: DifficultyDto): DifficultyDto

    /**
     * Update difficulty (admin only)
     * PUT /api/difficulties
     * @param difficulty Difficulty entity
     * @return Updated difficulty
     */
    @PUT("difficulties")
    suspend fun updateDifficulty(@Body difficulty: DifficultyDto): DifficultyDto

    /**
     * Delete difficulty by ID (admin only)
     * DELETE /api/difficulties/{difficultyId}
     * @param difficultyId Difficulty ID to delete
     * @return Deleted difficulty
     */
    @DELETE("difficulties/{difficultyId}")
    suspend fun deleteDifficulty(@Path("difficultyId") difficultyId: Int): DifficultyDto
}
