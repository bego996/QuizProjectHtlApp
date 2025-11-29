package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Quiz
import com.app.quizapp.domain.model.QuizQuestion
import com.app.quizapp.domain.util.Result

/**
 * Repository Interface für Quiz-Daten
 *
 * Das Repository ist die einzige Datenquelle für das ViewModel.
 * Es abstrahiert, woher die Daten kommen (Backend, Datenbank, Cache, etc.)
 *
 * Vorteile des Repository Patterns:
 * - Single Source of Truth
 * - Testbar (Interface kann gemockt werden)
 * - Kann mehrere Datenquellen kombinieren (z.B. Cache + Backend)
 * - Business-Logik ist unabhängig von der Datenquelle
 */
interface QuizRepository {

    /**
     * Lädt alle verfügbaren Quiz
     * @return Result mit Liste von Quiz oder Error
     */
    suspend fun getAllQuizzes(): Result<List<Quiz>>

    /**
     * Lädt ein bestimmtes Quiz
     * @param quizId Die ID des Quiz
     * @return Result mit Quiz oder Error
     */
    suspend fun getQuizById(quizId: Long): Result<Quiz>

    /**
     * Lädt zufällige Fragen
     * @param count Anzahl der Fragen
     * @param category Optional: Kategorie-Filter
     * @param difficulty Optional: Schwierigkeitsgrad-Filter
     * @return Result mit Liste von Fragen oder Error
     */
    suspend fun getRandomQuestions(
        count: Int = 10,
        category: String? = null,
        difficulty: String? = null
    ): Result<List<QuizQuestion>>

    /**
     * Lädt alle verfügbaren Kategorien
     * @return Result mit Liste von Kategorien oder Error
     */
    suspend fun getCategories(): Result<List<String>>
}
