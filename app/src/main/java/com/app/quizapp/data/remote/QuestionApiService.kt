package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.QuestionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface QuestionApiService {
    @GET("questions")
    suspend fun getAllQuestions(): List<QuestionDto>

    @GET("questions/{id}")
    suspend fun getQuestionById(@Path("id") id: Int): QuestionDto
}
