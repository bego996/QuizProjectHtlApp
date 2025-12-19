package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.LlmApiService
import com.app.quizapp.data.remote.dto.GenerateRequestDto
import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizResponseDto
import com.app.quizapp.domain.repository.LlmRepository
import com.app.quizapp.domain.util.Result
import com.app.quizapp.presentation.categories.Subtopic
import com.google.gson.JsonElement
import javax.inject.Inject

/**
 * Implementation of LlmRepository
 * Handles AI/LLM quiz generation operations (admin only)
 */
class LlmRepositoryImpl @Inject constructor(
    private val apiService: LlmApiService
) : LlmRepository {

    /**
     * Generate quiz question using AI/LLM with random parameters
     * @return Quiz response with generated quiz data
     */
    override suspend fun generateQuiz(): Result<QuizResponseDto> {
        return try {
            val response = apiService.generateQuiz()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to generate quiz")
        }
    }

    /**
     * Generate quiz question using AI/LLM with specific parameters
     * @param categoryId ID of the selected category (optional)
     * @param topicId ID of the selected topic (optional)
     * @param subtopicId ID of the selected subtopic (optional)
     * @param difficultyId ID of the selected difficulty (optional)
     * @return Quiz response with generated quiz data
     */
    override suspend fun generateQuiz(
        categoryId: Int?,
        topicId: Int?,
        subtopicId: Int?,
        difficultyId: Int?
    ): Result<QuizResponseDto> {
        return try {
            val response = apiService.generateQuiz(
                categoryId = categoryId,
                topicId = topicId,
                subtopicId = subtopicId,
                difficultyId = difficultyId
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to generate quiz with parameters")
        }
    }

    /**
     * Add generated quiz to database
     * @param topic Quiz topic
     * @param question Question text
     * @param difficulty Difficulty level
     * @param answers List of answer options
     * @param correctAnswer Index of correct answer
     * @return Saved quiz data
     */
    override suspend fun addQuizToDatabase(
        category: String,
        topic: String,
        subtopic: String,
        question: String,
        difficulty: String,
        answers: List<String>,
        correctAnswer: Int
    ): Result<QuizDto> {
        return try {
            val quizDto = QuizDto(
                category = category,
                topic = topic,
                subtopic,
                question = question,
                difficulty = difficulty,
                answers = answers,
                correctAnswer = correctAnswer
            )
            val response = apiService.addQuizToDatabase(quizDto)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add quiz to database")
        }
    }
}
