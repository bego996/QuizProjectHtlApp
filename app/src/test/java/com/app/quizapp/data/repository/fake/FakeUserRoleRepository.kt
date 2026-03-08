package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserRoleRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeUserRoleRepository : UserRoleRepository {

    // Test data storage
    private val userRoles = mutableListOf<UserRole>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllUserRoles(): Result<List<UserRole>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(userRoles.toList())
        }
    }

    override suspend fun getUserRoleById(userRoleId: Int): Result<UserRole> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userRole = userRoles.find { it.userRoleId == userRoleId }
            if (userRole != null) {
                Result.Success(userRole)
            } else {
                Result.Error("UserRole not found with id: $userRoleId")
            }
        }
    }

    override suspend fun createUserRole(userRole: UserRole): Result<UserRole> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newUserRole = userRole.copy(userRoleId = nextId++)
            userRoles.add(newUserRole)
            Result.Success(newUserRole)
        }
    }

    override suspend fun updateUserRole(userRole: UserRole): Result<UserRole> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = userRoles.indexOfFirst { it.userRoleId == userRole.userRoleId }
            if (index != -1) {
                userRoles[index] = userRole
                Result.Success(userRole)
            } else {
                Result.Error("UserRole not found with id: ${userRole.userRoleId}")
            }
        }
    }

    override suspend fun deleteUserRole(userRoleId: Int): Result<UserRole> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userRole = userRoles.find { it.userRoleId == userRoleId }
            if (userRole != null) {
                userRoles.remove(userRole)
                Result.Success(userRole)
            } else {
                Result.Error("UserRole not found with id: $userRoleId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test user role to repository
     */
    fun addTestUserRole(userRole: UserRole) {
        userRoles.add(userRole)
        if (userRole.userRoleId >= nextId) {
            nextId = userRole.userRoleId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        userRoles.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of user roles
     */
    fun getCount() = userRoles.size
}
