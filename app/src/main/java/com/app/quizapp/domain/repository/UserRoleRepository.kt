package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.util.Result

interface UserRoleRepository {
    suspend fun getAllUserRoles(): Result<List<UserRole>>
    suspend fun getUserRoleById(id: Int): Result<UserRole>
}
