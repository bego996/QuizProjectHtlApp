package com.app.quizapp.domain.security

/**
 * Interface for secure token management
 * Provides methods to store and retrieve authentication tokens securely
 */
interface TokenManager {
    /**
     * Saves the JWT token securely
     * @param token The JWT token string to save
     */
    suspend fun saveToken(token: String)

    /**
     * Retrieves the saved JWT token
     * @return The saved token or null if no token exists
     */
    suspend fun getToken(): String?

    /**
     * Checks if a valid token exists
     * @return true if a token is stored, false otherwise
     */
    suspend fun hasToken(): Boolean

    /**
     * Removes the saved token (logout)
     */
    suspend fun clearToken()
}
