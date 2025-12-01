package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.StatusDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StatusApiService {
    @GET("status")
    suspend fun getAllStatuses(): List<StatusDto>

    @GET("status/{id}")
    suspend fun getStatusById(@Path("id") id: Int): StatusDto
}
