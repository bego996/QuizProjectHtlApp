package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

/**
 * DTO for AI/LLM quiz generation request
 * Used for POST /api/llm/quiz/generate endpoint (Admin only)
 */
data class GenerateRequestDto(
    @SerializedName("model")
    val model: String,

    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("stream")
    val stream: Boolean = false,

    @SerializedName("format")
    val format: JsonElement? = null
)
