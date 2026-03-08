package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for submitting an answer to a quiz question
 * Used for PUT /api/users/me/quiz/submit/{userQuestionId} endpoint
 */
data class SubmitAnswerRequestDto(
    @SerializedName("answerId")
    val answerId: Int
)
