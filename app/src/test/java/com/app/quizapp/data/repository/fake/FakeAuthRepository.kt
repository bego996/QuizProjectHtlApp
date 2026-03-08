package com.app.quizapp.data.repository.fake

import com.app.quizapp.data.remote.dto.JwtResponseDto
import com.app.quizapp.domain.repository.AuthRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of AuthRepository for testing
 * Provides configurable authentication responses
 */
class FakeAuthRepository : AuthRepository {

    // Test data storage
    private val registeredUsers = mutableMapOf<String, String>() // email -> password

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"
    var mockJwtToken = "test-jwt-token"

    // ========== Auth Operations ==========

    override suspend fun login(email: String, password: String): Result<JwtResponseDto> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val storedPassword = registeredUsers[email]
            if (storedPassword != null && storedPassword == password) {
                Result.Success(
                    JwtResponseDto(
                        token = mockJwtToken,
                        type = "Bearer",
                        email = email
                    )
                )
            } else {
                Result.Error("Invalid email or password")
            }
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        firstname: String,
        surname: String,
        birthdate: String,
        nickname: String?
    ): Result<JwtResponseDto> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            if (registeredUsers.containsKey(email)) {
                Result.Error("Email already exists")
            } else {
                registeredUsers[email] = password
                Result.Success(
                    JwtResponseDto(
                        token = mockJwtToken,
                        type = "Bearer",
                        email = email
                    )
                )
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Pre-register a test user
     */
    fun addTestUser(email: String, password: String) {
        registeredUsers[email] = password
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        registeredUsers.clear()
        shouldReturnError = false
        mockJwtToken = "test-jwt-token"
    }

    /**
     * Get count of registered users
     */
    fun getCount() = registeredUsers.size
}
