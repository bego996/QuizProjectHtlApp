package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserRoleApiService
import com.app.quizapp.data.remote.dto.UserRoleDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of UserRoleRepository
 * Handles user role CRUD operations
 */
class UserRoleRepositoryImpl @Inject constructor(
    private val apiService: UserRoleApiService
) : UserRoleRepository {

    override suspend fun getAllUserRoles(): Result<List<UserRole>> {
        return try {
            val response = apiService.getAllUserRoles()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get user roles")
        }
    }

    override suspend fun getUserRoleById(userRoleId: Int): Result<UserRole> {
        return try {
            val response = apiService.getUserRoleById(userRoleId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get user role")
        }
    }

    override suspend fun createUserRole(userRole: UserRole): Result<UserRole> {
        return try {
            val userRoleDto = userRole.toDto()
            val response = apiService.createUserRole(userRoleDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create user role")
        }
    }

    override suspend fun updateUserRole(userRole: UserRole): Result<UserRole> {
        return try {
            val userRoleDto = userRole.toDto()
            val response = apiService.updateUserRole(userRoleDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update user role")
        }
    }

    override suspend fun deleteUserRole(userRoleId: Int): Result<UserRole> {
        return try {
            val response = apiService.deleteUserRole(userRoleId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete user role")
        }
    }

    // Helper function to convert UserRole domain model to DTO
    private fun UserRole.toDto(): UserRoleDto {
        return UserRoleDto(
            userRoleId = userRoleId,
            userRole = userRole
        )
    }
}
