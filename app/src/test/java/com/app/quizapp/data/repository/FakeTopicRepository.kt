package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of TopicRepository for testing purposes.
 */
class FakeTopicRepository : TopicRepository {

    private val testTopics = listOf(
        Topic(topicId = 1, topic = "Mathematics"),
        Topic(topicId = 2, topic = "Geography")
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var topicsToReturn = testTopics

    override suspend fun getAllTopics(): Result<List<Topic>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(topicsToReturn)
        }
    }

    override suspend fun getTopicById(id: Int): Result<Topic> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val topic = topicsToReturn.find { it.topicId == id }
            if (topic != null) {
                Result.Success(topic)
            } else {
                Result.Error("Topic with id $id not found")
            }
        }
    }
}
