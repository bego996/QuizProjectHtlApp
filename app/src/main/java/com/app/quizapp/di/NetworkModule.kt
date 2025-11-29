package com.app.quizapp.di

import com.app.quizapp.data.remote.QuizApiService
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
    private const val BASE_URL = "http://10.0.2.2:8080/"

    /**
     * Stellt den OkHttpClient bereit
     *
     * OkHttpClient ist verantwortlich für:
     * - HTTP-Verbindungen
     * - Interceptors (z.B. Logging, Auth-Header)
     * - Timeouts
     * - Connection Pooling
     *
     * @Provides sagt Hilt, dass diese Methode eine Dependency bereitstellt
     * @Singleton sorgt dafür, dass nur eine Instanz erstellt wird
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Logging Interceptor für Debug-Zwecke
        // Zeigt alle HTTP-Requests und Responses in den Logs an
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // BODY = komplette Request/Response Details
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Fügt Logging hinzu
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout für Verbindungsaufbau
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout für Daten empfangen
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
            .baseUrl(BASE_URL) // Basis-URL des Backends
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
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService {
        return retrofit.create(QuizApiService::class.java)
    }
}
