package com.app.quizapp.domain.model

data class UserQuestion(
    val userQuestionId: Int,
    val user: User,
    val question: Question,
    val score: Int
)
