package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for JWT authentication response
 * Returned after successful login or registration
 */
data class JwtResponseDto(
    @SerializedName("token")
    val token: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("email")
    val email: String
)
