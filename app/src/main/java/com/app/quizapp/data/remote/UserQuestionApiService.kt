package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.UserQuestionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserQuestionApiService {
    @GET("userQuestions")
    suspend fun getAllUserQuestions(): List<UserQuestionDto>

    @GET("userQuestions/{id}")
    suspend fun getUserQuestionById(@Path("id") id: Int): UserQuestionDto
}
