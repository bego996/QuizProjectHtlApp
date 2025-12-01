package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.repository.StatusRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of StatusRepository for testing purposes.
 */
class FakeStatusRepository : StatusRepository {

    private val testStatuses = listOf(
        Status(statusId = 1, text = "Active"),
        Status(statusId = 2, text = "Pending"),
        Status(statusId = 3, text = "Inactive")
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var statusesToReturn = testStatuses

    override suspend fun getAllStatuses(): Result<List<Status>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(statusesToReturn)
        }
    }

    override suspend fun getStatusById(id: Int): Result<Status> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val status = statusesToReturn.find { it.statusId == id }
            if (status != null) {
                Result.Success(status)
            } else {
                Result.Error("Status with id $id not found")
            }
        }
    }
}
