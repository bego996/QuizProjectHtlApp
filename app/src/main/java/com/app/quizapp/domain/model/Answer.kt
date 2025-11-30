package com.app.quizapp.domain.model

import com.app.quizapp.data.remote.dto.QuestionDto
import com.google.gson.annotations.SerializedName

/**
 * Domain Model für eine Answer
 *
 * Dies ist das Model, das in der gesamten App verwendet wird.
 * Es repräsentiert eine einzelne Quiz-Frage mit ihren Antwortmöglichkeiten.
 *
 * Domain Models sind unabhängig von der Datenquelle (Backend, Datenbank, etc.)
 * und definieren, wie Daten in der App-Logik verwendet werden.
 *
 * @property id Eindeutige ID der Frage
 * @property text Die Frage selbst
 * @property answers Liste der möglichen Antworten
 * @property correctAnswerIndex Index der korrekten Antwort (0-basiert)
 * @property category Kategorie der Frage (z.B. "Geschichte", "Wissenschaft")
 * @property difficulty Schwierigkeitsgrad (z.B. "EASY", "MEDIUM", "HARD")
 */
data class Answer(
    val answerId: Int,

    val text: String,

    val correct: Boolean,

    val question: Question,
)