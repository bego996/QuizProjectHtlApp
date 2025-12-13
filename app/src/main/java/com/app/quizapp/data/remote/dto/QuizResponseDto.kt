package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for AI/LLM quiz generation response
 * Returned from POST /api/llm/quiz/generate endpoint
 */
data class QuizResponseDto(
    @SerializedName("model")
    val model: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("quiz")
    val quiz: QuizDto
)
