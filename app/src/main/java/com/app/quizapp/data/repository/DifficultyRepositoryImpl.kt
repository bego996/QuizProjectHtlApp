package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.DifficultyApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class DifficultyRepositoryImpl @Inject constructor(
    private val apiService: DifficultyApiService
) : DifficultyRepository {

    override suspend fun getAllDifficulties(): Result<List<Difficulty>> {
        return try {
            val response = apiService.getAllDifficulties()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getDifficultyById(id: Int): Result<Difficulty> {
        return try {
            val response = apiService.getDifficultyById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
