package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.util.Result

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
}
