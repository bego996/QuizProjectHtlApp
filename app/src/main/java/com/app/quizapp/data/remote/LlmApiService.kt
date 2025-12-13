package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.GenerateRequestDto
import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service for AI/LLM quiz generation endpoints
 * Base path: /api/llm
 * All endpoints require ROLE_admin
 */
interface LlmApiService {

    /**
     * Generate quiz question using AI/LLM (Ollama)
     * POST /api/llm/quiz/generate
     * Returns reactive Mono response
     * @param generateRequest LLM generation parameters (model, prompt, stream, format)
     * @return QuizResponseDto with generated quiz data
     */
    @POST("api/llm/quiz/generate")
    suspend fun generateQuiz(@Body generateRequest: GenerateRequestDto): QuizResponseDto

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
