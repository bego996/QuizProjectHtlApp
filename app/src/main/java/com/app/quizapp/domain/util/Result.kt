package com.app.quizapp.domain.util

/**
 * Result Wrapper für Erfolg/Fehler Handling
 *
 * Diese sealed class ermöglicht typsicheres Error Handling.
 * Jeder API-Call gibt entweder Success oder Error zurück.
 *
 * Vorteile:
 * - Typsicher: Compiler erzwingt Fehlerbehandlung
 * - Keine Exceptions im normalen Flow
 * - Einfach zu testen
 * - Klare Trennung zwischen Erfolg und Fehler
 *
 * Verwendung:
 * ```
 * when (val result = repository.getQuiz()) {
 *     is Result.Success -> println(result.data)
 *     is Result.Error -> println(result.message)
 * }
 * ```
 */
sealed class Result<out T> {
    /**
     * Erfolgreicher API-Call
     * @param data Die geladenen Daten
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Fehlgeschlagener API-Call
     * @param message Fehlermeldung für den Nutzer
     * @param throwable Optional: Die ursprüngliche Exception
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Result<Nothing>()
}
