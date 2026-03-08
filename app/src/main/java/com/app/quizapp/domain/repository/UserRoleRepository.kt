package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for UserRole operations
 * Provides CRUD operations for user role management
 */
interface UserRoleRepository {

    /**
     * Get all user roles (max 10)
     * @return List of user roles
     */
    suspend fun getAllUserRoles(): Result<List<UserRole>>

    /**
     * Get user role by ID
     * @param userRoleId UserRole ID
     * @return UserRole entity
     */
    suspend fun getUserRoleById(userRoleId: Int): Result<UserRole>

    /**
     * Create new user role (admin only)
     * @param userRole UserRole entity
     * @return Created user role
     */
    suspend fun createUserRole(userRole: UserRole): Result<UserRole>

    /**
     * Update user role (admin only)
     * @param userRole UserRole entity with updates
     * @return Updated user role
     */
    suspend fun updateUserRole(userRole: UserRole): Result<UserRole>

    /**
     * Delete user role (admin only)
     * @param userRoleId UserRole ID to delete
     * @return Deleted user role
     */
    suspend fun deleteUserRole(userRoleId: Int): Result<UserRole>
}
