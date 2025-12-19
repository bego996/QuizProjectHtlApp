package com.app.quizapp.domain.repository

import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizResponseDto
import com.app.quizapp.domain.util.Result
import com.app.quizapp.presentation.categories.Subtopic
import com.google.gson.JsonElement

/**
 * Repository interface for AI/LLM quiz generation operations
 * All operations require admin role
 */
interface LlmRepository {

    /**
     * Generate quiz question using AI/LLM
     * @return Quiz response with generated quiz data
     */
    suspend fun generateQuiz(): Result<QuizResponseDto>

    /**
     * Add generated quiz to database
     * @param topic Quiz topic
     * @param question Question text
     * @param difficulty Difficulty level
     * @param answers List of answer options
     * @param correctAnswer Index of correct answer
     * @return Saved quiz data
     */
    suspend fun addQuizToDatabase(
        category: String,
        topic: String,
        subtopic: String,
        question: String,
        difficulty: String,
        answers: List<String>,
        correctAnswer: Int
    ): Result<QuizDto>
}
