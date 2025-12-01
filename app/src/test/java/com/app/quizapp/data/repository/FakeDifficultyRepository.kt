package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of DifficultyRepository for testing purposes.
 */
class FakeDifficultyRepository : DifficultyRepository {

    private val testDifficulties = listOf(
        Difficulty(difficultyId = 1, mode = "Easy"),
        Difficulty(difficultyId = 2, mode = "Medium"),
        Difficulty(difficultyId = 3, mode = "Hard")
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var difficultiesToReturn = testDifficulties

    override suspend fun getAllDifficulties(): Result<List<Difficulty>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(difficultiesToReturn)
        }
    }

    override suspend fun getDifficultyById(id: Int): Result<Difficulty> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val difficulty = difficultiesToReturn.find { it.difficultyId == id }
            if (difficulty != null) {
                Result.Success(difficulty)
            } else {
                Result.Error("Difficulty with id $id not found")
            }
        }
    }
}
