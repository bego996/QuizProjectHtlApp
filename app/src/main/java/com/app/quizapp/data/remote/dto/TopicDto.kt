package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Topic
import com.google.gson.annotations.SerializedName

data class TopicDto(
    @SerializedName("topicId")
    val topicId: Int,

    @SerializedName("topic")
    val topic: String,
)

fun TopicDto.toDomain(): Topic {
    return Topic(
        topicId = topicId,
        topic = topic
    )
}
