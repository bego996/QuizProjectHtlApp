package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.AnswerDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnswerApiService {


    /**
     * API Service Interface für Quiz-Backend Kommunikation
     *
     * Dieses Interface definiert alle API-Endpunkte für Answer des Quiz-Backend.
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

        /**
         * Lädt alle verfügbaren Answer
         *
         * Beispiel-Endpunkt: GET http://10.0.2.2:8080/answers
         *
         * @return Liste aller Answers
         */
        @GET("answers")
        suspend fun getAllAnswers(): List<AnswerDto>

        /**
         * Lädt ein bestimmtes Answer anhand seiner ID
         *
         * Beispiel-Endpunkt: GET http://10.0.2.2:8080/answers/1
         *
         * @param answerId Die ID des gewünschten Quiz
         * @return Das Answer
         */
        @GET("answers/{id}")
        suspend fun getAnswerById(@Path("id") answerId: Int): AnswerDto
}