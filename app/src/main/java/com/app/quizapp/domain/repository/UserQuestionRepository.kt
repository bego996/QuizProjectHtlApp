package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.util.Result

interface UserQuestionRepository {
    suspend fun getAllUserQuestions(): Result<List<UserQuestion>>
    suspend fun getUserQuestionById(id: Int): Result<UserQuestion>
}
