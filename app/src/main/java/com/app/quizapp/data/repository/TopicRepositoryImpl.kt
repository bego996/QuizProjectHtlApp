package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.TopicApiService
import com.app.quizapp.data.remote.dto.TopicDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of TopicRepository
 * Handles topic CRUD operations
 */
class TopicRepositoryImpl @Inject constructor(
    private val apiService: TopicApiService
) : TopicRepository {

    override suspend fun getAllTopics(): Result<List<Topic>> {
        return try {
            val response = apiService.getAllTopics()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get topics")
        }
    }

    override suspend fun getTopicById(topicId: Int): Result<Topic> {
        return try {
            val response = apiService.getTopicById(topicId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get topic")
        }
    }

    override suspend fun createTopic(topic: Topic): Result<Topic> {
        return try {
            val topicDto = topic.toDto()
            val response = apiService.createTopic(topicDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create topic")
        }
    }

    override suspend fun updateTopic(topic: Topic): Result<Topic> {
        return try {
            val topicDto = topic.toDto()
            val response = apiService.updateTopic(topicDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update topic")
        }
    }

    override suspend fun deleteTopic(topicId: Int): Result<Topic> {
        return try {
            val response = apiService.deleteTopic(topicId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete topic")
        }
    }

    // Helper function to convert Topic domain model to DTO
    private fun Topic.toDto(): TopicDto {
        return TopicDto(
            topicId = topicId,
            topic = topic
        )
    }
}
