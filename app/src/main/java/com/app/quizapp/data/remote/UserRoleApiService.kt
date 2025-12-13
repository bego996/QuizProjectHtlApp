package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.UserRoleDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for UserRole CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface UserRoleApiService {

    /**
     * Get all user roles (max 10 with HATEOAS links)
     * GET /api/userRoles
     * @return List of user roles
     */
    @GET("api/userRoles")
    suspend fun getAllUserRoles(): List<UserRoleDto>

    /**
     * Get user role by ID
     * GET /api/userRoles/{userRoleId}
     * @param userRoleId UserRole ID
     * @return UserRole with HATEOAS links
     */
    @GET("api/userRoles/{userRoleId}")
    suspend fun getUserRoleById(@Path("userRoleId") userRoleId: Int): UserRoleDto

    /**
     * Create new user role (admin only)
     * POST /api/userRoles
     * @param userRole UserRole entity
     * @return Created user role
     */
    @POST("api/userRoles")
    suspend fun createUserRole(@Body userRole: UserRoleDto): UserRoleDto

    /**
     * Update user role (admin only)
     * PUT /api/userRoles
     * @param userRole UserRole entity
     * @return Updated user role
     */
    @PUT("api/userRoles")
    suspend fun updateUserRole(@Body userRole: UserRoleDto): UserRoleDto

    /**
     * Delete user role by ID (admin only)
     * DELETE /api/userRoles/{userRoleId}
     * @param userRoleId UserRole ID to delete
     * @return Deleted user role
     */
    @DELETE("api/userRoles/{userRoleId}")
    suspend fun deleteUserRole(@Path("userRoleId") userRoleId: Int): UserRoleDto
}
