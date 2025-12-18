package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of TopicRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeTopicRepository : TopicRepository {

    // Test data storage
    private val topics = mutableListOf<Topic>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllTopics(): Result<List<Topic>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(topics.toList())
        }
    }

    override suspend fun getAllHighestTopics(): Result<List<Topic>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            // Return topics without parent (highest level topics)
            Result.Success(topics.filter { it.parentTopic == null })
        }
    }

    override suspend fun getTopicById(topicId: Int): Result<Topic> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val topic = topics.find { it.topicId == topicId }
            if (topic != null) {
                Result.Success(topic)
            } else {
                Result.Error("Topic not found with id: $topicId")
            }
        }
    }

    override suspend fun createTopic(topic: Topic): Result<Topic> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newTopic = topic.copy(topicId = nextId++)
            topics.add(newTopic)
            Result.Success(newTopic)
        }
    }

    override suspend fun updateTopic(topic: Topic): Result<Topic> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = topics.indexOfFirst { it.topicId == topic.topicId }
            if (index != -1) {
                topics[index] = topic
                Result.Success(topic)
            } else {
                Result.Error("Topic not found with id: ${topic.topicId}")
            }
        }
    }

    override suspend fun deleteTopic(topicId: Int): Result<Topic> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val topic = topics.find { it.topicId == topicId }
            if (topic != null) {
                topics.remove(topic)
                Result.Success(topic)
            } else {
                Result.Error("Topic not found with id: $topicId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test topic to repository
     */
    fun addTestTopic(topic: Topic) {
        topics.add(topic)
        if (topic.topicId >= nextId) {
            nextId = topic.topicId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        topics.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of topics
     */
    fun getCount() = topics.size
}
