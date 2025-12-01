package com.app.quizapp.domain.model

data class User(
    val userId: Int,
    val surname: String,
    val firstname: String,
    val birthdate: String,
    val nickname: String,
    val email: String,
    val password: String,
    val userRole: UserRole
)
