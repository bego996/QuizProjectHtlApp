package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserQuestionApiService
import com.app.quizapp.data.remote.dto.DifficultyDto
import com.app.quizapp.data.remote.dto.QuestionDto
import com.app.quizapp.data.remote.dto.StatusDto
import com.app.quizapp.data.remote.dto.TopicDto
import com.app.quizapp.data.remote.dto.UserDto
import com.app.quizapp.data.remote.dto.UserQuestionDto
import com.app.quizapp.data.remote.dto.UserRoleDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of UserQuestionRepository
 * Handles user question/quiz attempt CRUD operations (admin only)
 */
class UserQuestionRepositoryImpl @Inject constructor(
    private val apiService: UserQuestionApiService
) : UserQuestionRepository {

    override suspend fun getAllUserQuestions(): Result<List<UserQuestion>> {
        return try {
            val response = apiService.getAllUserQuestions()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get user questions")
        }
    }

    override suspend fun getUserQuestionById(userQuestionId: Int): Result<UserQuestion> {
        return try {
            val response = apiService.getUserQuestionById(userQuestionId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get user question")
        }
    }

    override suspend fun createUserQuestion(userQuestion: UserQuestion): Result<UserQuestion> {
        return try {
            val userQuestionDto = userQuestion.toDto()
            val response = apiService.createUserQuestion(userQuestionDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create user question")
        }
    }

    override suspend fun updateUserQuestion(userQuestion: UserQuestion): Result<UserQuestion> {
        return try {
            val userQuestionDto = userQuestion.toDto()
            val response = apiService.updateUserQuestion(userQuestionDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update user question")
        }
    }

    override suspend fun deleteUserQuestion(userQuestionId: Int): Result<UserQuestion> {
        return try {
            val response = apiService.deleteUserQuestion(userQuestionId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete user question")
        }
    }

    override suspend fun deleteUserQuestionsByUserId(userId: Int): Result<Unit> {
        return try {
            apiService.deleteUserQuestionsByUserId(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete user questions")
        }
    }

    // Helper function to convert UserQuestion domain model to DTO
    private fun UserQuestion.toDto(): UserQuestionDto {
        return UserQuestionDto(
            userQuestionId = userQuestionId,
            user = UserDto(
                userId = user.userId,
                surname = user.surname,
                firstname = user.firstname,
                birthdate = user.birthdate,
                nickname = user.nickname,
                email = user.email,
                password = user.password,
                userRole = UserRoleDto(
                    userRoleId = user.userRole.userRoleId,
                    userRole = user.userRole.userRole
                )
            ),
            question = QuestionDto(
                questionId = question.questionId,
                questionText = question.questionText,
                reviewedBy = question.reviewedBy,
                topic = TopicDto(
                    topicId = question.topic.topicId,
                    topic = question.topic.topic
                ),
                status = StatusDto(
                    statusId = question.status.statusId,
                    text = question.status.text
                ),
                difficulty = DifficultyDto(
                    difficultyId = question.difficulty.difficultyId,
                    mode = question.difficulty.mode
                ),
                createdAt = question.createdAt,
            ),
            score = score
        )
    }
}
