package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Question
import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("questionId")
    val questionId: Int,

    @SerializedName("questionText")
    val questionText: String,

    @SerializedName("reviewedBy")
    val reviewedBy: Int? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("topic")
    val topic: TopicDto,

    @SerializedName("status")
    val status: StatusDto,

    @SerializedName("difficulty")
    val difficulty: DifficultyDto
)

fun QuestionDto.toDomain(): Question {
    return Question(
        questionId = questionId,
        questionText = questionText,
        reviewedBy = reviewedBy,
        createdAt = createdAt,
        topic = topic.toDomain(),
        status = status.toDomain(),
        difficulty = difficulty.toDomain()
    )
}
