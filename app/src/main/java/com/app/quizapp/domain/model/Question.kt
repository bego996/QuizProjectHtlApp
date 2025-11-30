package com.app.quizapp.domain.model

data class Question(
    val questionId: Int,
    val questionText: String,
    val reviewedBy: Int,
    val topic: Topic,
    val status: Status,
    val difficulty: Difficulty
)
