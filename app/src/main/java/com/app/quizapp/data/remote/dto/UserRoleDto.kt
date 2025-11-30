package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.UserRole
import com.google.gson.annotations.SerializedName

data class UserRoleDto(
    @SerializedName("userRoleId")
    val userRoleId: Int,

    @SerializedName("userRole")
    val userRole: String
)

fun UserRoleDto.toDomain(): UserRole {
    return UserRole(
        userRoleId = userRoleId,
        userRole = userRole
    )
}
