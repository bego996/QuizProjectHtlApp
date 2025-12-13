package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.AnswerApiService
import com.app.quizapp.data.remote.dto.AnswerDto
import com.app.quizapp.data.remote.dto.QuestionDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AnswerRepository
 * Handles answer CRUD operations
 */
@Singleton
class AnswerRepositoryImpl @Inject constructor(
    private val apiService: AnswerApiService
) : AnswerRepository {

    override suspend fun getAllAnswers(): Result<List<Answer>> {
        return try {
            val response = apiService.getAllAnswers()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(
                message = "Failed to get answers: ${e.localizedMessage ?: "Unknown error"}",
                throwable = e
            )
        }
    }

    override suspend fun getAnswerById(answerId: Int): Result<Answer> {
        return try {
            val response = apiService.getAnswerById(answerId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(
                message = "Failed to get answer: ${e.localizedMessage ?: "Unknown error"}",
                throwable = e
            )
        }
    }

    override suspend fun createAnswer(answer: Answer): Result<Answer> {
        return try {
            val answerDto = answer.toDto()
            val response = apiService.createAnswer(answerDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(
                message = "Failed to create answer: ${e.localizedMessage ?: "Unknown error"}",
                throwable = e
            )
        }
    }

    override suspend fun updateAnswer(answer: Answer): Result<Answer> {
        return try {
            val answerDto = answer.toDto()
            val response = apiService.updateAnswer(answerDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(
                message = "Failed to update answer: ${e.localizedMessage ?: "Unknown error"}",
                throwable = e
            )
        }
    }

    override suspend fun deleteAnswer(answerId: Int): Result<Answer> {
        return try {
            val response = apiService.deleteAnswer(answerId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(
                message = "Failed to delete answer: ${e.localizedMessage ?: "Unknown error"}",
                throwable = e
            )
        }
    }

    // Helper function to convert Answer domain model to DTO
    private fun Answer.toDto(): AnswerDto {
        return AnswerDto(
            answerId = answerId,
            text = text,
            correct = correct,
            question = QuestionDto(
                questionId = question.questionId,
                questionText = question.questionText,
                reviewedBy = question.reviewedBy,
                topic = com.app.quizapp.data.remote.dto.TopicDto(
                    topicId = question.topic.topicId,
                    topic = question.topic.topic
                ),
                status = com.app.quizapp.data.remote.dto.StatusDto(
                    statusId = question.status.statusId,
                    text = question.status.text
                ),
                difficulty = com.app.quizapp.data.remote.dto.DifficultyDto(
                    difficultyId = question.difficulty.difficultyId,
                    mode = question.difficulty.mode
                )
            )
        )
    }
}