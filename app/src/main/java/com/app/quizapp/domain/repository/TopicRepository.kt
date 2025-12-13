package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for Topic operations
 * Provides CRUD operations for topic management
 */
interface TopicRepository {

    /**
     * Get all topics (max 10)
     * @return List of topics
     */
    suspend fun getAllTopics(): Result<List<Topic>>

    /**
     * Get topic by ID
     * @param topicId Topic ID
     * @return Topic entity
     */
    suspend fun getTopicById(topicId: Int): Result<Topic>

    /**
     * Create new topic (admin only)
     * @param topic Topic entity
     * @return Created topic
     */
    suspend fun createTopic(topic: Topic): Result<Topic>

    /**
     * Update topic (admin only)
     * @param topic Topic entity with updates
     * @return Updated topic
     */
    suspend fun updateTopic(topic: Topic): Result<Topic>

    /**
     * Delete topic (admin only)
     * @param topicId Topic ID to delete
     * @return Deleted topic
     */
    suspend fun deleteTopic(topicId: Int): Result<Topic>
}
