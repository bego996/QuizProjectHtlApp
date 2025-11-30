package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.util.Result

interface QuestionRepository {
    suspend fun getAllQuestions(): Result<List<Question>>
    suspend fun getQuestionById(id: Int): Result<Question>
}
