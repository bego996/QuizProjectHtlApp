package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.QuestionApiService
import com.app.quizapp.data.remote.dto.DifficultyDto
import com.app.quizapp.data.remote.dto.QuestionDto
import com.app.quizapp.data.remote.dto.StatusDto
import com.app.quizapp.data.remote.dto.TopicDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of QuestionRepository
 * Handles question operations with filtering and CRUD
 */
class QuestionRepositoryImpl @Inject constructor(
    private val apiService: QuestionApiService
) : QuestionRepository {

    override suspend fun getAllQuestions(
        topicId: Int?,
        difficultyId: Int?,
        statusId: Int?
    ): Result<List<Question>> {
        return try {
            val response = apiService.getAllQuestions(topicId, difficultyId, statusId)
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get questions")
        }
    }

    override suspend fun getQuestionById(questionId: Int): Result<Question> {
        return try {
            val response = apiService.getQuestionById(questionId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get question")
        }
    }

    override suspend fun createQuestion(question: Question): Result<Question> {
        return try {
            val questionDto = question.toDto()
            val response = apiService.createQuestion(questionDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create question")
        }
    }

    override suspend fun updateQuestion(question: Question): Result<Question> {
        return try {
            val questionDto = question.toDto()
            val response = apiService.updateQuestion(questionDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update question")
        }
    }

    override suspend fun deleteQuestion(questionId: Int): Result<Question> {
        return try {
            val response = apiService.deleteQuestion(questionId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete question")
        }
    }

    // Helper function to convert Question domain model to DTO
    private fun Question.toDto(): QuestionDto {
        return QuestionDto(
            questionId = questionId,
            questionText = questionText,
            reviewedBy = reviewedBy,
            topic = TopicDto(
                topicId = topic.topicId,
                topic = topic.topic
            ),
            status = StatusDto(
                statusId = status.statusId,
                text = status.text
            ),
            difficulty = DifficultyDto(
                difficultyId = difficulty.difficultyId,
                mode = difficulty.mode
            ),
            createdAt = createdAt
        )
    }
}

