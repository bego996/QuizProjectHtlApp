package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = apiService.getAllUsers()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return try {
            val response = apiService.getUserById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
