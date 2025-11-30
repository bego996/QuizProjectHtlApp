package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.QuestionApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val apiService: QuestionApiService
) : QuestionRepository {

    override suspend fun getAllQuestions(): Result<List<Question>> {
        return try {
            val response = apiService.getAllQuestions()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getQuestionById(id: Int): Result<Question> {
        return try {
            val response = apiService.getQuestionById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
