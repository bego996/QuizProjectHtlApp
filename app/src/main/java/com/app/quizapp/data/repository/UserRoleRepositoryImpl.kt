package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserRoleApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class UserRoleRepositoryImpl @Inject constructor(
    private val apiService: UserRoleApiService
) : UserRoleRepository {

    override suspend fun getAllUserRoles(): Result<List<UserRole>> {
        return try {
            val response = apiService.getAllUserRoles()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getUserRoleById(id: Int): Result<UserRole> {
        return try {
            val response = apiService.getUserRoleById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
