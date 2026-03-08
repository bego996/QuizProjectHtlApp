package com.app.quizapp.data.remote

import com.app.quizapp.data.remote.dto.JwtResponseDto
import com.app.quizapp.data.remote.dto.LoginRequestDto
import com.app.quizapp.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service for authentication endpoints
 * Base path: /api/auth
 * All endpoints are public (no authentication required)
 */
interface AuthApiService {

    /**
     * Login with email and password
     * POST /api/auth/login
     * @param loginRequest User credentials (email, password)
     * @return JWT token response with email
     */
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): JwtResponseDto

    /**
     * Register a new user account
     * POST /api/auth/register
     * Auto-login after successful registration
     * @param registerRequest User registration data
     * @return JWT token response with email
     */
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequestDto): JwtResponseDto
}
