package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of DifficultyRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeDifficultyRepository : DifficultyRepository {

    // Test data storage
    private val difficulties = mutableListOf<Difficulty>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllDifficulties(): Result<List<Difficulty>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(difficulties.toList())
        }
    }

    override suspend fun getDifficultyById(difficultyId: Int): Result<Difficulty> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val difficulty = difficulties.find { it.difficultyId == difficultyId }
            if (difficulty != null) {
                Result.Success(difficulty)
            } else {
                Result.Error("Difficulty not found with id: $difficultyId")
            }
        }
    }

    override suspend fun createDifficulty(difficulty: Difficulty): Result<Difficulty> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newDifficulty = difficulty.copy(difficultyId = nextId++)
            difficulties.add(newDifficulty)
            Result.Success(newDifficulty)
        }
    }

    override suspend fun updateDifficulty(difficulty: Difficulty): Result<Difficulty> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = difficulties.indexOfFirst { it.difficultyId == difficulty.difficultyId }
            if (index != -1) {
                difficulties[index] = difficulty
                Result.Success(difficulty)
            } else {
                Result.Error("Difficulty not found with id: ${difficulty.difficultyId}")
            }
        }
    }

    override suspend fun deleteDifficulty(difficultyId: Int): Result<Difficulty> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val difficulty = difficulties.find { it.difficultyId == difficultyId }
            if (difficulty != null) {
                difficulties.remove(difficulty)
                Result.Success(difficulty)
            } else {
                Result.Error("Difficulty not found with id: $difficultyId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test difficulty to repository
     */
    fun addTestDifficulty(difficulty: Difficulty) {
        difficulties.add(difficulty)
        if (difficulty.difficultyId >= nextId) {
            nextId = difficulty.difficultyId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        difficulties.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of difficulties
     */
    fun getCount() = difficulties.size
}
