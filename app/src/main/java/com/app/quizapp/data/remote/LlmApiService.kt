package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.GenerateRequestDto
import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API service for AI/LLM quiz generation endpoints
 * Base path: /api/llm
 * All endpoints require ROLE_admin
 */
interface LlmApiService {

    /**
     * Generate quiz question using AI/LLM (Ollama) with random parameters
     * POST /api/llm/quiz/generate
     * Returns reactive Mono response
     * @return QuizResponseDto with generated quiz data
     */
    @POST("api/llm/quiz/generate")
    suspend fun generateQuiz(): QuizResponseDto

    /**
     * Generate quiz question using AI/LLM (Ollama) with specific parameters
     * POST /api/llm/quiz/generate
     * @param categoryId Category ID (optional)
     * @param topicId Topic ID (optional)
     * @param subtopicId Subtopic ID (optional)
     * @param difficultyId Difficulty ID (optional)
     * @return QuizResponseDto with generated quiz data
     */
    @POST("api/llm/quiz/generate")
    suspend fun generateQuiz(
        @Query("categoryId") categoryId: Int? = null,
        @Query("topicId") topicId: Int? = null,
        @Query("subtopicId") subtopicId: Int? = null,
        @Query("difficultyId") difficultyId: Int? = null
    ): QuizResponseDto

    /**
     * Add generated quiz to database
     * POST /api/llm/quiz/add
     * Saves quiz question with answers to the database
     * @param quiz Quiz data (topic, question, difficulty, answers, correct_answer)
     * @return Saved QuizDto
     */
    @POST("api/llm/quiz/add")
    suspend fun addQuizToDatabase(@Body quiz: QuizDto): QuizDto
}
