package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserQuestionApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class UserQuestionRepositoryImpl @Inject constructor(
    private val apiService: UserQuestionApiService
) : UserQuestionRepository {

    override suspend fun getAllUserQuestions(): Result<List<UserQuestion>> {
        return try {
            val response = apiService.getAllUserQuestions()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getUserQuestionById(id: Int): Result<UserQuestion> {
        return try {
            val response = apiService.getUserQuestionById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
