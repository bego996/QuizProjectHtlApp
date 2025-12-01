package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.util.Result

/**
 * Repository Interface für Answer-Daten
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

interface AnswerRepository {
    /**
     * Lädt alle verfügbaren Answer
     * @return Result mit Liste von Answer oder Error
     */
    suspend fun getAllAnswers(): Result<List<Answer>>

    /**
     * Lädt ein bestimmtes Answer
     * @param answerId Die ID des Quiz
     * @return Result mit Answer oder Error
     */
    suspend fun geAnswerById(answerId: Int): Result<Answer>

}