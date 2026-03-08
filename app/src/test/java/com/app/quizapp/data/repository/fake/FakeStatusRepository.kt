package com.app.quizapp.data.repository.fake

import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.repository.StatusRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of StatusRepository for testing
 * Provides in-memory storage and configurable error responses
 */
class FakeStatusRepository : StatusRepository {

    // Test data storage
    private val statuses = mutableListOf<Status>()
    private var nextId = 1

    // Configuration for test scenarios
    var shouldReturnError = false
    var errorMessage = "Test error"

    // ========== CRUD Operations ==========

    override suspend fun getAllStatuses(): Result<List<Status>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(statuses.toList())
        }
    }

    override suspend fun getStatusById(statusId: Int): Result<Status> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val status = statuses.find { it.statusId == statusId }
            if (status != null) {
                Result.Success(status)
            } else {
                Result.Error("Status not found with id: $statusId")
            }
        }
    }

    override suspend fun createStatus(status: Status): Result<Status> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val newStatus = status.copy(statusId = nextId++)
            statuses.add(newStatus)
            Result.Success(newStatus)
        }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val index = statuses.indexOfFirst { it.statusId == status.statusId }
            if (index != -1) {
                statuses[index] = status
                Result.Success(status)
            } else {
                Result.Error("Status not found with id: ${status.statusId}")
            }
        }
    }

    override suspend fun deleteStatus(statusId: Int): Result<Status> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val status = statuses.find { it.statusId == statusId }
            if (status != null) {
                statuses.remove(status)
                Result.Success(status)
            } else {
                Result.Error("Status not found with id: $statusId")
            }
        }
    }

    // ========== Test Helper Functions ==========

    /**
     * Add test status to repository
     */
    fun addTestStatus(status: Status) {
        statuses.add(status)
        if (status.statusId >= nextId) {
            nextId = status.statusId + 1
        }
    }

    /**
     * Clear all test data
     */
    fun clearTestData() {
        statuses.clear()
        nextId = 1
        shouldReturnError = false
    }

    /**
     * Get count of statuses
     */
    fun getCount() = statuses.size
}
