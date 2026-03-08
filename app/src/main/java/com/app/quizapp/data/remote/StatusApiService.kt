package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.StatusDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for Status CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface StatusApiService {

    /**
     * Get all statuses (max 10 with HATEOAS links)
     * GET /api/status
     * @return List of statuses
     */
    @GET("api/status")
    suspend fun getAllStatuses(): List<StatusDto>

    /**
     * Get status by ID
     * GET /api/status/{statusId}
     * @param statusId Status ID
     * @return Status with HATEOAS links
     */
    @GET("api/status/{statusId}")
    suspend fun getStatusById(@Path("statusId") statusId: Int): StatusDto

    /**
     * Create new status (admin only)
     * POST /api/status
     * @param status Status entity
     * @return Created status
     */
    @POST("api/status")
    suspend fun createStatus(@Body status: StatusDto): StatusDto

    /**
     * Update status (admin only)
     * PUT /api/status
     * @param status Status entity
     * @return Updated status
     */
    @PUT("api/status")
    suspend fun updateStatus(@Body status: StatusDto): StatusDto

    /**
     * Delete status by ID (admin only)
     * DELETE /api/status/{statusId}
     * @param statusId Status ID to delete
     * @return Deleted status
     */
    @DELETE("api/status/{statusId}")
    suspend fun deleteStatus(@Path("statusId") statusId: Int): StatusDto
}
