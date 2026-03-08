package com.app.quizapp.domain.security

/**
 * Fake implementation of TokenManager for testing
 * Provides in-memory token storage for testing ViewModels
 */
class FakeTokenManager : TokenManager {

    // In-memory token storage
    private var storedToken: String? = null

    override suspend fun saveToken(token: String) {
        storedToken = token
    }

    override suspend fun getToken(): String? {
        return storedToken
    }

    override suspend fun hasToken(): Boolean {
        return storedToken != null
    }

    override suspend fun clearToken() {
        storedToken = null
    }

    // ========== Test Helper Functions ==========

    /**
     * Clear token (for test cleanup)
     */
    fun reset() {
        storedToken = null
    }

    /**
     * Get stored token for verification in tests
     */
    fun getStoredToken() = storedToken
}
