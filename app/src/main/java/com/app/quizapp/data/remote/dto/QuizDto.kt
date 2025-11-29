package com.app.quizapp.data.remote.dto

import com.app.quizapp.domain.model.Quiz
import com.app.quizapp.domain.model.QuizQuestion
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
data class QuizQuestionDto(
    @SerializedName("questionId")
    val id: Int,

    @SerializedName("questionText")
    val text: String,

    @SerializedName("reviewedBy")
    val reviewedBy: Int,

    @SerializedName("userQuestion")
    val userQuestion: List<String>,

    @SerializedName("category")
    val category: String,

    @SerializedName("difficulty")
    val difficulty: String
)

/**
 * DTO für ein komplettes Quiz
 */
data class UserQuestions(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("questions")
    val questions: List<QuizQuestionDto>
)

/**
 * Extension Function zum Umwandeln von DTO zu Domain Model
 *
 * Diese Funktion konvertiert das DTO (vom Backend) in unser Domain Model (für die App).
 * Dadurch bleibt die App-Logik unabhängig von der Backend-Struktur.
 */
//fun QuizQuestionDto.toDomain(): QuizQuestion {
//    return QuizQuestion(
//        id = id,
//        text = text,
//        answers = answers,
//        correctAnswerIndex = correctAnswerIndex,
//        category = category,
//        difficulty = difficulty
//    )
//}
//
///**
// * Extension Function zum Umwandeln von Quiz DTO zu Domain Model
// */
//fun QuizDto.toDomain(): Quiz {
//    return Quiz(
//        id = id,
//        title = title,
//        questions = questions.map { it.toDomain() }
//    )
//}
