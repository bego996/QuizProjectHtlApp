package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Difficulty
import com.google.gson.annotations.SerializedName

data class DifficultyDto(
    @SerializedName("difficultyId")
    val difficultyId: Int,

    @SerializedName("mode")
    val mode: String
)

fun DifficultyDto.toDomain(): Difficulty {
    return Difficulty(
        difficultyId = difficultyId,
        mode = mode
    )
}
