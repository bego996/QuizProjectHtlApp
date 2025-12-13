package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for updating user profile
 * Used for PUT /api/users/me endpoint
 * @param birthdate Format: yyyy-MM-dd (LocalDate format)
 */
data class UpdateProfileRequestDto(
    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("nickname")
    val nickname: String? = null,

    @SerializedName("birthdate")
    val birthdate: String
)
