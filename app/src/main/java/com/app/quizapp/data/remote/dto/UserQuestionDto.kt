package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.UserQuestion
import com.google.gson.annotations.SerializedName

data class UserQuestionDto(
    @SerializedName("userQuestionId")
    val userQuestionId: Int,

    @SerializedName("user")
    val user: UserDto,

    @SerializedName("question")
    val question: QuestionDto,

    @SerializedName("score")
    val score: Int,
)

fun UserQuestionDto.toDomain(): UserQuestion {
    return UserQuestion(
        userQuestionId = userQuestionId,
        user = user.toDomain(),
        question = question.toDomain(),
        score = score
    )
}
