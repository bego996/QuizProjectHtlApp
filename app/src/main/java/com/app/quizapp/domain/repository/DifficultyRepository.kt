package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for Difficulty operations
 * Provides CRUD operations for difficulty management
 */
interface DifficultyRepository {

    /**
     * Get all difficulties (max 10)
     * @return List of difficulties
     */
    suspend fun getAllDifficulties(): Result<List<Difficulty>>

    /**
     * Get difficulty by ID
     * @param difficultyId Difficulty ID
     * @return Difficulty entity
     */
    suspend fun getDifficultyById(difficultyId: Int): Result<Difficulty>

    /**
     * Create new difficulty (admin only)
     * @param difficulty Difficulty entity
     * @return Created difficulty
     */
    suspend fun createDifficulty(difficulty: Difficulty): Result<Difficulty>

    /**
     * Update difficulty (admin only)
     * @param difficulty Difficulty entity with updates
     * @return Updated difficulty
     */
    suspend fun updateDifficulty(difficulty: Difficulty): Result<Difficulty>

    /**
     * Delete difficulty (admin only)
     * @param difficultyId Difficulty ID to delete
     * @return Deleted difficulty
     */
    suspend fun deleteDifficulty(difficultyId: Int): Result<Difficulty>
}
