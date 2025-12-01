package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.DifficultyDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DifficultyApiService {
    @GET("difficulties")
    suspend fun getAllDifficulties(): List<DifficultyDto>

    @GET("difficulties/{id}")
    suspend fun getDifficultyById(@Path("id") id: Int): DifficultyDto
}
