package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for user registration request
 * Maps to backend RegisterRequestDto
 * @param birthdate Format: dd.MM.yyyy (e.g., "15.06.2000")
 */
data class RegisterRequestDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("birthdate")
    val birthdate: String,

    @SerializedName("nickname")
    val nickname: String? = null
)
