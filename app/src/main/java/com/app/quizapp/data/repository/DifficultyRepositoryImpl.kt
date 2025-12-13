package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.DifficultyApiService
import com.app.quizapp.data.remote.dto.DifficultyDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of DifficultyRepository
 * Handles difficulty CRUD operations
 */
class DifficultyRepositoryImpl @Inject constructor(
    private val apiService: DifficultyApiService
) : DifficultyRepository {

    override suspend fun getAllDifficulties(): Result<List<Difficulty>> {
        return try {
            val response = apiService.getAllDifficulties()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get difficulties")
        }
    }

    override suspend fun getDifficultyById(difficultyId: Int): Result<Difficulty> {
        return try {
            val response = apiService.getDifficultyById(difficultyId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get difficulty")
        }
    }

    override suspend fun createDifficulty(difficulty: Difficulty): Result<Difficulty> {
        return try {
            val difficultyDto = difficulty.toDto()
            val response = apiService.createDifficulty(difficultyDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create difficulty")
        }
    }

    override suspend fun updateDifficulty(difficulty: Difficulty): Result<Difficulty> {
        return try {
            val difficultyDto = difficulty.toDto()
            val response = apiService.updateDifficulty(difficultyDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update difficulty")
        }
    }

    override suspend fun deleteDifficulty(difficultyId: Int): Result<Difficulty> {
        return try {
            val response = apiService.deleteDifficulty(difficultyId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete difficulty")
        }
    }

    // Helper function to convert Difficulty domain model to DTO
    private fun Difficulty.toDto(): DifficultyDto {
        return DifficultyDto(
            difficultyId = difficultyId,
            mode = mode
        )
    }
}
