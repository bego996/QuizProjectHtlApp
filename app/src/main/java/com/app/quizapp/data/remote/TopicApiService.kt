package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.TopicDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TopicApiService {
    @GET("topics")
    suspend fun getAllTopics(): List<TopicDto>

    @GET("topics/{id}")
    suspend fun getTopicById(@Path("id") id: Int): TopicDto
}
