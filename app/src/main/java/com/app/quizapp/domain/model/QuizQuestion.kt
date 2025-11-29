package com.app.quizapp.domain.model

/**
 * Domain Model für eine Quiz-Frage
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
data class QuizQuestion(
    val id: Long,
    val text: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val category: String,
    val difficulty: String
)

/**
 * Domain Model für ein komplettes Quiz
 *
 * Repräsentiert eine Sammlung von Quiz-Fragen, z.B. für eine Quiz-Session
 *
 * @property id Eindeutige ID des Quiz
 * @property title Titel des Quiz
 * @property questions Liste der Fragen in diesem Quiz
 */
data class Quiz(
    val id: Long,
    val title: String,
    val questions: List<QuizQuestion>
)
