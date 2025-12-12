package com.app.quizapp.data.security

import com.app.quizapp.domain.security.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp Interceptor that automatically adds JWT token to HTTP requests
 * Adds "Authorization: Bearer <token>" header to authenticated requests
 */
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    /**
     * Intercepts HTTP requests and adds Authorization header if token exists
     * Uses runBlocking because OkHttp interceptors are synchronous
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get token synchronously (OkHttp interceptors can't be suspend)
        val token = runBlocking { tokenManager.getToken() }

        // If no token exists, proceed without Authorization header
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
