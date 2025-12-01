package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.UserRoleDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRoleApiService {
    @GET("userRoles")
    suspend fun getAllUserRoles(): List<UserRoleDto>

    @GET("userRoles/{id}")
    suspend fun getUserRoleById(@Path("id") id: Int): UserRoleDto
}
