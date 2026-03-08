package com.app.quizapp.domain.repository

import com.app.quizapp.data.remote.dto.JwtResponseDto
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for authentication operations
 * Handles login and registration
 */
interface AuthRepository {

    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password
     * @return JWT response with token and user email
     */
    suspend fun login(email: String, password: String): Result<JwtResponseDto>

    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param firstname User first name
     * @param surname User surname
     * @param birthdate User birthdate (format: dd.MM.yyyy)
     * @param nickname Optional nickname
     * @return JWT response with token and user email (auto-login)
     */
    suspend fun register(
        email: String,
        password: String,
        firstname: String,
        surname: String,
        birthdate: String,
        nickname: String? = null
    ): Result<JwtResponseDto>
}
