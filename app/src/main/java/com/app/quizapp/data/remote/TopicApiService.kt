package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.TopicDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service for Topic CRUD operations
 * All endpoints require authentication (admin for POST/PUT/DELETE)
 */
interface TopicApiService {

    /**
     * Get all topics (max 10 with HATEOAS links)
     * GET /api/topics
     * @return List of topics
     */
    @GET("api/topics")
    suspend fun getAllTopics(): List<TopicDto>

    /**
     * Get topic by ID
     * GET /api/topics/{topicId}
     * @param topicId Topic ID
     * @return Topic with HATEOAS links
     */
    @GET("api/topics/{topicId}")
    suspend fun getTopicById(@Path("topicId") topicId: Int): TopicDto

    /**
     * Create new topic (admin only)
     * POST /api/topics
     * @param topic Topic entity
     * @return Created topic
     */
    @POST("api/topics")
    suspend fun createTopic(@Body topic: TopicDto): TopicDto

    /**
     * Update topic (admin only)
     * PUT /api/topics
     * @param topic Topic entity
     * @return Updated topic
     */
    @PUT("api/topics")
    suspend fun updateTopic(@Body topic: TopicDto): TopicDto

    /**
     * Delete topic by ID (admin only)
     * DELETE /api/topics/{topicId}
     * @param topicId Topic ID to delete
     * @return Deleted topic
     */
    @DELETE("api/topics/{topicId}")
    suspend fun deleteTopic(@Path("topicId") topicId: Int): TopicDto
}
