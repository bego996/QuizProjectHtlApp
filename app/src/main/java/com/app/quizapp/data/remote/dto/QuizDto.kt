package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for AI-generated quiz data
 * Used in LLM endpoints for quiz generation and adding to database
 * @param correctAnswer Index of the correct answer in the answers list
 */
data class QuizDto(
    @SerializedName("category")
    val category: String,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("subtopic")
    val subtopic: String,

    @SerializedName("question")
    val question: String,

    @SerializedName("difficulty")
    val difficulty: String,

    @SerializedName("answers")
    val answers: List<String>,

    @SerializedName("correct_answer")
    val correctAnswer: Int
)
