package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserRoleRepository for testing purposes.
 */
class FakeUserRoleRepository : UserRoleRepository {

    private val testUserRoles = listOf(
        UserRole(userRoleId = 1, userRole = "Student"),
        UserRole(userRoleId = 2, userRole = "Teacher"),
        UserRole(userRoleId = 3, userRole = "Admin")
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var userRolesToReturn = testUserRoles

    override suspend fun getAllUserRoles(): Result<List<UserRole>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(userRolesToReturn)
        }
    }

    override suspend fun getUserRoleById(id: Int): Result<UserRole> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userRole = userRolesToReturn.find { it.userRoleId == id }
            if (userRole != null) {
                Result.Success(userRole)
            } else {
                Result.Error("UserRole with id $id not found")
            }
        }
    }
}
