package com.app.quizapp.di

import com.app.quizapp.data.remote.AnswerApiService
import com.app.quizapp.data.remote.AuthApiService
import com.app.quizapp.data.remote.DifficultyApiService
import com.app.quizapp.data.remote.LlmApiService
import com.app.quizapp.data.remote.QuestionApiService
import com.app.quizapp.data.remote.StatusApiService
import com.app.quizapp.data.remote.TopicApiService
import com.app.quizapp.data.remote.UserApiService
import com.app.quizapp.data.remote.UserQuestionApiService
import com.app.quizapp.data.remote.UserRoleApiService
import com.app.quizapp.data.security.AuthInterceptor
import com.app.quizapp.domain.security.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module für Netzwerk-Komponenten
 *
 * Dieses Modul stellt alle Abhängigkeiten für die Backend-Kommunikation bereit:
 * - OkHttpClient: HTTP-Client mit Logging und Timeouts
 * - Retrofit: REST-Client für API-Calls
 * - QuizApiService: Interface für die Quiz-API Endpunkte
 *
 * @Module markiert diese Klasse als Hilt-Modul
 * @InstallIn(SingletonComponent::class) bedeutet, dass diese Dependencies
 * während der gesamten App-Laufzeit verfügbar sind (Singleton)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Base URL des Backend-Servers
     *
     * WICHTIG für Entwicklung:
     * - Android Emulator: Nutze "http://10.0.2.2:PORT" statt "localhost"
     *   (10.0.2.2 ist die spezielle IP-Adresse, die vom Emulator auf den Host-Computer zeigt)
     * - Physisches Gerät: Nutze die lokale IP-Adresse deines Computers (z.B. "http://192.168.1.100:PORT")
     *
     * TODO: Passe den Port an dein Spring Boot Backend an
     */
    private const val BASE_URL_EMULATOR_TO_PC = "http://10.0.2.2:8080/"
    private const val BASE_URL_PHYSICAL_TO_PC = "http://192.168.0.242:8080/"

    /**
     * Stellt den OkHttpClient bereit
     *
     * OkHttpClient ist verantwortlich für:
     * - HTTP-Verbindungen
     * - Interceptors (z.B. Logging, Auth-Header)
     * - Timeouts
     * - Connection Pooling
     *
     * @param tokenManager Verwaltet JWT-Token für Authentication
     * @Provides sagt Hilt, dass diese Methode eine Dependency bereitstellt
     * @Singleton sorgt dafür, dass nur eine Instanz erstellt wird
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        // Auth Interceptor für automatisches Hinzufügen von JWT-Token
        val authInterceptor = AuthInterceptor(tokenManager)

        // Logging Interceptor für Debug-Zwecke
        // Zeigt alle HTTP-Requests und Responses in den Logs an
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // BODY = komplette Request/Response Details
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)    // Fügt JWT-Token zu Requests hinzu
            .addInterceptor(loggingInterceptor) // Fügt Logging hinzu
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout für Verbindungsaufbau
            .readTimeout(60, TimeUnit.SECONDS)    // Timeout für Daten empfangen
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout für Daten senden
            .build()
    }

    /**
     * Stellt Retrofit bereit
     *
     * Retrofit ist die Hauptkomponente für REST-API Kommunikation.
     * Es konvertiert unser API-Interface (QuizApiService) in funktionierende HTTP-Calls.
     *
     * @param okHttpClient Der HTTP-Client, der für die Requests verwendet wird
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_EMULATOR_TO_PC) // Basis-URL des Backends
            .client(okHttpClient) // Nutzt unseren konfigurierten OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // JSON zu Kotlin-Objekten konvertieren
            .build()
    }

    /**
     * Stellt das QuizApiService Interface bereit
     *
     * Retrofit erstellt automatisch eine Implementierung des Interfaces
     * basierend auf den Annotationen (@GET, @POST, etc.)
     *
     * @param retrofit Die Retrofit-Instanz
     */
    @Provides
    @Singleton
    fun provideAnswerApiService(retrofit: Retrofit): AnswerApiService {
        return retrofit.create(AnswerApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRoleApiService(retrofit: Retrofit): UserRoleApiService {
        return retrofit.create(UserRoleApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDifficultyApiService(retrofit: Retrofit): DifficultyApiService {
        return retrofit.create(DifficultyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStatusApiService(retrofit: Retrofit): StatusApiService {
        return retrofit.create(StatusApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTopicApiService(retrofit: Retrofit): TopicApiService {
        return retrofit.create(TopicApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionApiService(retrofit: Retrofit): QuestionApiService {
        return retrofit.create(QuestionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserQuestionApiService(retrofit: Retrofit): UserQuestionApiService {
        return retrofit.create(UserQuestionApiService::class.java)
    }

    /**
     * Stellt das AuthApiService Interface bereit
     * Für Login und Registrierung (öffentliche Endpoints)
     */
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    /**
     * Stellt das LlmApiService Interface bereit
     * Für AI/LLM Quiz-Generierung (Admin)
     */
    @Provides
    @Singleton
    fun provideLlmApiService(retrofit: Retrofit): LlmApiService {
        return retrofit.create(LlmApiService::class.java)
    }
}
