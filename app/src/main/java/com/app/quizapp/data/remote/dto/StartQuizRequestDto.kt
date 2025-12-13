package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for starting a quiz attempt
 * Used for POST /api/users/me/quiz/start endpoint
 */
data class StartQuizRequestDto(
    @SerializedName("questionId")
    val questionId: Int
)
