package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.StatusApiService
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.repository.StatusRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

class StatusRepositoryImpl @Inject constructor(
    private val apiService: StatusApiService
) : StatusRepository {

    override suspend fun getAllStatuses(): Result<List<Status>> {
        return try {
            val response = apiService.getAllStatuses()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }

    override suspend fun getStatusById(id: Int): Result<Status> {
        return try {
            val response = apiService.getStatusById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
        }
    }
}
