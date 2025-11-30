package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.util.Result

interface StatusRepository {
    suspend fun getAllStatuses(): Result<List<Status>>
    suspend fun getStatusById(id: Int): Result<Status>
}
