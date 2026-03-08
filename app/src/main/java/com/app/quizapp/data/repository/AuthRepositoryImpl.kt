package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.AuthApiService
import com.app.quizapp.data.remote.dto.JwtResponseDto
import com.app.quizapp.data.remote.dto.LoginRequestDto
import com.app.quizapp.data.remote.dto.RegisterRequestDto
import com.app.quizapp.domain.repository.AuthRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of AuthRepository
 * Handles authentication operations via API
 */
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {

    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password
     * @return JWT response with token
     */
    override suspend fun login(email: String, password: String): Result<JwtResponseDto> {
        return try {
            val request = LoginRequestDto(email = email, password = password)
            val response = apiService.login(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    /**
     * Register new user account
     * @param email User email
     * @param password User password
     * @param firstname User first name
     * @param surname User surname
     * @param birthdate User birthdate (dd.MM.yyyy)
     * @param nickname Optional nickname
     * @return JWT response with token (auto-login)
     */
    override suspend fun register(
        email: String,
        password: String,
        firstname: String,
        surname: String,
        birthdate: String,
        nickname: String?
    ): Result<JwtResponseDto> {
        return try {
            val request = RegisterRequestDto(
                email = email,
                password = password,
                firstname = firstname,
                surname = surname,
                birthdate = birthdate,
                nickname = nickname
            )
            val response = apiService.register(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }
}
