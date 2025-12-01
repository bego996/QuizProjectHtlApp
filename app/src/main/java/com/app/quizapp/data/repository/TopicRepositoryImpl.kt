package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.TopicApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val apiService: TopicApiService
) : TopicRepository {

    override suspend fun getAllTopics(): Result<List<Topic>> {
        return try {
            val response = apiService.getAllTopics()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getTopicById(id: Int): Result<Topic> {
        return try {
            val response = apiService.getTopicById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
