package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.StatusApiService
import com.app.quizapp.data.remote.dto.StatusDto
import com.app.quizapp.data.remote.dto.toDomain
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.repository.StatusRepository
import com.app.quizapp.domain.util.Result
import javax.inject.Inject

/**
 * Implementation of StatusRepository
 * Handles status CRUD operations
 */
class StatusRepositoryImpl @Inject constructor(
    private val apiService: StatusApiService
) : StatusRepository {

    override suspend fun getAllStatuses(): Result<List<Status>> {
        return try {
            val response = apiService.getAllStatuses()
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get statuses")
        }
    }

    override suspend fun getStatusById(statusId: Int): Result<Status> {
        return try {
            val response = apiService.getStatusById(statusId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get status")
        }
    }

    override suspend fun createStatus(status: Status): Result<Status> {
        return try {
            val statusDto = status.toDto()
            val response = apiService.createStatus(statusDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create status")
        }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return try {
            val statusDto = status.toDto()
            val response = apiService.updateStatus(statusDto)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update status")
        }
    }

    override suspend fun deleteStatus(statusId: Int): Result<Status> {
        return try {
            val response = apiService.deleteStatus(statusId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete status")
        }
    }

    // Helper function to convert Status domain model to DTO
    private fun Status.toDto(): StatusDto {
        return StatusDto(
            statusId = statusId,
            text = text
        )
    }
}
