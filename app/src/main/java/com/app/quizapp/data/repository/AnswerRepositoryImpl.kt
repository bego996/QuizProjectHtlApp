package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.AnswerApiService
//import com.app.quizapp.data.remote.QuizApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementierung des AnswerRepository
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

@Singleton
class AnswerRepositoryImpl @Inject constructor(
    private val apiService: AnswerApiService
) : AnswerRepository  {

    /**
     * Lädt alle Answer vom Backend
     */
    override suspend fun getAllAnswers(): Result<List<Answer>> {
        return try {
             //Api Call durchführen
            val answerDtos = apiService.getAllAnswers()

            //Dtos in Domain Models umwandeln
            val answers = answerDtos.map { it.toDomain() }

            //Erfolg zurückgeben
            Result.Success(answers)
        } catch (e: Exception) {
            // Fehlerbehandlung mit nutzerfreundlicher Nachricht
            Result.Error(
                message = "Fehler beim Laden der Quiz: ${e.localizedMessage ?: "Unbekannter Fehler"}",
                throwable = e
            )
        }
    }


    /**
     * Lädt ein bestimmtes Answer vom Backend
     */
    override suspend fun geAnswerById(answerId: Int): Result<Answer> {
        return try {
            val answerDto = apiService.getAnswerById(answerId)
            val answer = answerDto.toDomain()
            Result.Success(answer)
        }catch (e: Exception){
            Result.Error(
                message = "Fehler beim Laden des Quiz: ${e.localizedMessage ?: "Unbekannter Fehler"}",
                throwable = e
            )
        }
    }

}