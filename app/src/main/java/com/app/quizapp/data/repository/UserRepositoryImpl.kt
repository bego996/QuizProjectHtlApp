package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserApiService
import com.app.quizapp.data.remote.dto.ChangePasswordRequestDto
import com.app.quizapp.data.remote.dto.StartQuizRequestDto
import com.app.quizapp.data.remote.dto.SubmitAnswerRequestDto
import com.app.quizapp.data.remote.dto.UpdateProfileRequestDto
import com.app.quizapp.data.remote.dto.UserDto
import com.app.quizapp.data.remote.dto.UserRoleDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of UserRepository
 * Handles user-related operations including admin CRUD, profile, and quiz
 */
class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {

    // ============================================
    // Admin Operations
    // ============================================

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = apiService.getAllUsers()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get users")
        }
    }

    override suspend fun getUserById(userId: Int): Result<User> {
        return try {
            val response = apiService.getUserById(userId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get user")
        }
    }

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val userDto = user.toDto()
            val response = apiService.createUser(userDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create user")
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userDto = user.toDto()
            val response = apiService.updateUser(userDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update user")
        }
    }

    override suspend fun deleteUser(userId: Int): Result<User> {
        return try {
            val response = apiService.deleteUser(userId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete user")
        }
    }

    // ============================================
    // User Profile Operations
    // ============================================

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get current user")
        }
    }

    override suspend fun updateCurrentUser(
        firstname: String,
        surname: String,
        nickname: String?,
        birthdate: String
    ): Result<User> {
        return try {
            val request = UpdateProfileRequestDto(
                firstname = firstname,
                surname = surname,
                nickname = nickname,
                birthdate = birthdate
            )
            val response = apiService.updateCurrentUser(request)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return try {
            val request = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            val response = apiService.changePassword(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to change password")
        }
    }

    // ============================================
    // Quiz Operations
    // ============================================

    override suspend fun getQuizAttempts(): Result<List<UserQuestion>> {
        return try {
            val response = apiService.getQuizAttempts()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get quiz attempts")
        }
    }

    override suspend fun getQuizAttemptById(userQuestionId: Int): Result<UserQuestion> {
        return try {
            val response = apiService.getQuizAttemptById(userQuestionId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get quiz attempt")
        }
    }

    override suspend fun startQuiz(questionId: Int): Result<UserQuestion> {
        return try {
            val request = StartQuizRequestDto(questionId = questionId)
            val response = apiService.startQuiz(request)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to start quiz")
        }
    }

    override suspend fun submitAnswer(userQuestionId: Int, answerId: Int): Result<UserQuestion> {
        return try {
            val request = SubmitAnswerRequestDto(answerId = answerId)
            val response = apiService.submitAnswer(userQuestionId, request)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to submit answer")
        }
    }

    // Helper function to convert User domain model to DTO
    private fun User.toDto(): UserDto {
        return UserDto(
            userId = userId,
            surname = surname,
            firstname = firstname,
            birthdate = birthdate,
            nickname = nickname,
            email = email,
            password = password,
            userRole = UserRoleDto(
                userRoleId = userRole.userRoleId,
                userRole = userRole.userRole
            )
        )
    }
}
