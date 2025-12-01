package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.util.Result

interface DifficultyRepository {
    suspend fun getAllDifficulties(): Result<List<Difficulty>>
    suspend fun getDifficultyById(id: Int): Result<Difficulty>
}
