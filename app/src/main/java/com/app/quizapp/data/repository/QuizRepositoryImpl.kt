package com.app.quizapp.data.repository

//import com.app.quizapp.data.remote.QuizApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Quiz
import com.app.quizapp.domain.model.QuizQuestion
import com.app.quizapp.domain.repository.QuizRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementierung des QuizRepository
 *
 * Diese Klasse implementiert das Repository Interface und kommuniziert
 * mit dem Backend über das QuizApiService.
 *
 * @Inject Constructor Injection: Hilt injiziert automatisch das QuizApiService
 * @Singleton Nur eine Instanz dieser Klasse während der App-Laufzeit
 *
 * Fehlerbehandlung:
 * - Alle API-Calls sind in try-catch Blöcken
 * - Bei Erfolg: Result.Success mit den Daten
 * - Bei Fehler: Result.Error mit einer nutzerfreundlichen Meldung
 */
//@Singleton
//class QuizRepositoryImpl @Inject constructor(
//    private val apiService: QuizApiService
//) : QuizRepository {
//
//    /**
//     * Lädt alle Quiz vom Backend
//     */
//    override suspend fun getAllQuizzes(): Result<List<Quiz>> {
//        return try {
//            // API-Call durchführen
//            val quizDtos = apiService.getAllQuizzes()
//
//            // DTOs in Domain Models umwandeln
//            val quizzes = quizDtos.map { it.toDomain() }
//
//            // Erfolg zurückgeben
//            Result.Success(quizzes)
//
//        } catch (e: Exception) {
//            // Fehlerbehandlung mit nutzerfreundlicher Nachricht
//            Result.Error(
//                message = "Fehler beim Laden der Quiz: ${e.localizedMessage ?: "Unbekannter Fehler"}",
//                throwable = e
//            )
//        }
//    }
//
//    /**
//     * Lädt ein bestimmtes Quiz vom Backend
//     */
//    override suspend fun getQuizById(quizId: Long): Result<Quiz> {
//        return try {
//            val quizDto = apiService.getQuizById(quizId)
//            val quiz = quizDto.toDomain()
//            Result.Success(quiz)
//
//        } catch (e: Exception) {
//            Result.Error(
//                message = "Fehler beim Laden des Quiz: ${e.localizedMessage ?: "Unbekannter Fehler"}",
//                throwable = e
//            )
//        }
//    }
//
//    /**
//     * Lädt zufällige Fragen vom Backend
//     */
//    override suspend fun getRandomQuestions(
//        count: Int,
//        category: String?,
//        difficulty: String?
//    ): Result<List<QuizQuestion>> {
//        return try {
//            val questionDtos = apiService.getRandomQuestions(count, category, difficulty)
//            val questions = questionDtos.map { it.toDomain() }
//            Result.Success(questions)
//
//        } catch (e: Exception) {
//            Result.Error(
//                message = "Fehler beim Laden der Fragen: ${e.localizedMessage ?: "Unbekannter Fehler"}",
//                throwable = e
//            )
//        }
//    }
//
//    /**
//     * Lädt alle Kategorien vom Backend
//     */
//    override suspend fun getCategories(): Result<List<String>> {
//        return try {
//            val categories = apiService.getCategories()
//            Result.Success(categories)
//
//        } catch (e: Exception) {
//            Result.Error(
//                message = "Fehler beim Laden der Kategorien: ${e.localizedMessage ?: "Unbekannter Fehler"}",
//                throwable = e
//            )
//        }
//    }
//}
