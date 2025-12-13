package com.app.quizapp.domain.repository

import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizResponseDto
import com.app.quizapp.domain.util.Result
import com.google.gson.JsonElement

/**
 * Repository interface for AI/LLM quiz generation operations
 * All operations require admin role
 */
interface LlmRepository {

    /**
     * Generate quiz question using AI/LLM
     * @param model LLM model name (e.g., "llama2")
     * @param prompt Generation prompt
     * @param stream Whether to stream response
     * @param format Optional JSON format specification
     * @return Quiz response with generated quiz data
     */
    suspend fun generateQuiz(
        model: String,
        prompt: String,
        stream: Boolean = false,
        format: JsonElement? = null
    ): Result<QuizResponseDto>

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
        topic: String,
        question: String,
        difficulty: String,
        answers: List<String>,
        correctAnswer: Int
    ): Result<QuizDto>
}
