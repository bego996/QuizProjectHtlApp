package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for Status operations
 * Provides CRUD operations for status management
 */
interface StatusRepository {

    /**
     * Get all statuses (max 10)
     * @return List of statuses
     */
    suspend fun getAllStatuses(): Result<List<Status>>

    /**
     * Get status by ID
     * @param statusId Status ID
     * @return Status entity
     */
    suspend fun getStatusById(statusId: Int): Result<Status>

    /**
     * Create new status (admin only)
     * @param status Status entity
     * @return Created status
     */
    suspend fun createStatus(status: Status): Result<Status>

    /**
     * Update status (admin only)
     * @param status Status entity with updates
     * @return Updated status
     */
    suspend fun updateStatus(status: Status): Result<Status>

    /**
     * Delete status (admin only)
     * @param statusId Status ID to delete
     * @return Deleted status
     */
    suspend fun deleteStatus(statusId: Int): Result<Status>
}
