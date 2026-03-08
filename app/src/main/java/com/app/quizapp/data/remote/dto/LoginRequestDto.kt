package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for login request
 * Maps to backend LoginRequestDto
 */
data class LoginRequestDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
