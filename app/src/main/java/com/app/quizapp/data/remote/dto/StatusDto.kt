package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Status
import com.google.gson.annotations.SerializedName

data class StatusDto(
    @SerializedName("statusId")
    val statusId: Int,

    @SerializedName("text")
    val text: String
)

fun StatusDto.toDomain(): Status {
    return Status(
        statusId = statusId,
        text = text
    )
}
