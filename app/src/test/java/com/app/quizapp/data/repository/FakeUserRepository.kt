package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserRepository for testing purposes.
 * Allows configuration of success/error scenarios without needing real API calls.
 */
class FakeUserRepository : UserRepository {

    // Configurable test data
    private val testUserRole = UserRole(userRoleId = 1, userRole = "Student")
    private val testUsers = listOf(
        User(
            userId = 1,
            surname = "Mustermann",
            firstname = "Max",
            birthdate = "2000-01-01",
            nickname = "maxm",
            email = "max@test.com",
            password = "test123",
            userRole = testUserRole
        ),
        User(
            userId = 2,
            surname = "Doe",
            firstname = "Jane",
            birthdate = "1999-05-15",
            nickname = "janed",
            email = "jane@test.com",
            password = "test456",
            userRole = testUserRole
        )
    )

    // Configurable behavior
    var shouldReturnError = false
    var errorMessage = "Test error"
    var usersToReturn = testUsers

    override suspend fun getAllUsers(): Result<List<User>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(usersToReturn)
        }
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val user = usersToReturn.find { it.userId == id }
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error("User with id $id not found")
            }
        }
    }
}
