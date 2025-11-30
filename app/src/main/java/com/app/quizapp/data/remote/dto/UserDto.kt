package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("birthdate")
    val birthdate: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("userRole")
    val userRole: UserRoleDto
)

fun UserDto.toDomain(): User {
    return User(
        userId = userId,
        surname = surname,
        firstname = firstname,
        birthdate = birthdate,
        nickname = nickname,
        email = email,
        password = password,
        userRole = userRole.toDomain()
    )
}
