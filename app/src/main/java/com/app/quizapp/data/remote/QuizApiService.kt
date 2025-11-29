package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.QuizDto
import com.app.quizapp.data.remote.dto.QuizQuestionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service Interface für Quiz-Backend Kommunikation
 *
 * Dieses Interface definiert alle API-Endpunkte für das Quiz-Backend.
 * Retrofit generiert automatisch die Implementierung basierend auf den Annotationen.
 *
 * Wichtige Retrofit Annotationen:
 * - @GET, @POST, @PUT, @DELETE: HTTP-Methoden
 * - @Path: Variable im URL-Pfad (z.B. /quiz/{id})
 * - @Query: Query-Parameter (z.B. /quiz?category=science)
 * - @Body: Request-Body für POST/PUT
 *
 * Alle Funktionen sind suspend functions, damit sie in Coroutines laufen können
 * (asynchrone Ausführung ohne Thread-Blocking)
 *
 * TODO: Passe die Endpunkte an dein Spring Boot Backend an!
 */
interface QuizApiService {

    /**
     * Lädt alle verfügbaren Quiz
     *
     * Beispiel-Endpunkt: GET http://10.0.2.2:8080/api/quiz
     *
     * @return Liste aller Quiz
     */
    @GET("api/quiz")
    suspend fun getAllQuizzes(): List<QuizDto>

    /**
     * Lädt ein bestimmtes Quiz anhand seiner ID
     *
     * Beispiel-Endpunkt: GET http://10.0.2.2:8080/api/quiz/1
     *
     * @param quizId Die ID des gewünschten Quiz
     * @return Das Quiz mit allen Fragen
     */
    @GET("api/quiz/{id}")
    suspend fun getQuizById(@Path("id") quizId: Long): QuizDto

    /**
     * Lädt zufällige Quiz-Fragen
     *
     * Beispiel-Endpunkt: GET http://10.0.2.2:8080/api/questions/random?count=5&category=science
     *
     * @param count Anzahl der gewünschten Fragen (default: 10)
     * @param category Optional: Nur Fragen aus dieser Kategorie
     * @param difficulty Optional: Schwierigkeitsgrad (EASY, MEDIUM, HARD)
     * @return Liste von zufälligen Fragen
     */
    @GET("api/questions/random")
    suspend fun getRandomQuestions(
        @Query("count") count: Int = 10,
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null
    ): List<QuizQuestionDto>

    /**
     * Lädt alle verfügbaren Kategorien
     *
     * Beispiel-Endpunkt: GET http://10.0.2.2:8080/api/categories
     *
     * @return Liste aller Kategorien
     */
    @GET("api/categories")
    suspend fun getCategories(): List<String>

    // Weitere Endpunkte können hier hinzugefügt werden, z.B.:
    // - @POST("api/quiz/{id}/submit") suspend fun submitQuizResults(...)
    // - @GET("api/leaderboard") suspend fun getLeaderboard()
    // - @POST("api/user/score") suspend fun saveScore(...)
}
