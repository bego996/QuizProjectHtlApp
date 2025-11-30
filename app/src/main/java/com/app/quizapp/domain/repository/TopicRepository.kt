package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.util.Result

interface TopicRepository {
    suspend fun getAllTopics(): Result<List<Topic>>
    suspend fun getTopicById(id: Int): Result<Topic>
}
