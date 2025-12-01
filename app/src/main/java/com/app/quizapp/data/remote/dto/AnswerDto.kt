package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.model.Topic
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) für Quiz-Fragen vom Backend
 *
 * DTOs sind Datenklassen, die die JSON-Struktur des Backends abbilden.
 * Sie werden nur für die Netzwerk-Kommunikation verwendet und dann
 * in Domain Models umgewandelt.
 *
 * @SerializedName mappt JSON-Feldnamen auf Kotlin-Properties
 * (nützlich wenn Backend andere Namenskonventionen nutzt, z.B. snake_case)
 *
 * TODO: Passe diese Struktur an dein Spring Boot Backend an!
 */
data class AnswerDto(
    @SerializedName("answerId")
    val answerId: Int,

    @SerializedName("text")
    val text: String,

    @SerializedName("correct")
    val correct: Boolean,

    @SerializedName("question")
    val question: QuestionDto,
)


/**
 * Extension Functions zum Umwandeln von DTO zu Domain Model
 *
 * Diese Funktion konvertiert das DTO (vom Backend) in unser Domain Model (für die App).
 * Dadurch bleibt die App-Logik unabhängig von der Backend-Struktur.
 */
fun AnswerDto.toDomain(): Answer {
    return Answer(
        answerId = answerId,
        text = text,
        correct = correct,
        question = question.toDomain()
    )
}
