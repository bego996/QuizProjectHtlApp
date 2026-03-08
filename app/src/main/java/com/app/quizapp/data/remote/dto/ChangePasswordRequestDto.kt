package com.app.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for changing user password
 * Used for PUT /api/users/me/password endpoint
 */
data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,

    @SerializedName("newPassword")
    val newPassword: String
)
