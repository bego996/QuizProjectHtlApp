package com.app.quizapp.domain.model

import java.time.LocalDate

data class Question(
    val questionId: Int,
    val questionText: String,
    val reviewedBy: Int? = null,
    val createdAt: String,
    val topic: Topic,
    val status: Status,
    val difficulty: Difficulty
)
