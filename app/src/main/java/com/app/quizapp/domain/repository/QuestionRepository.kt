package com.app.quizapp.domain.repository

import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.util.Result

/**
 * Repository interface for question operations
 * Supports filtering and admin CRUD operations
 */
interface QuestionRepository {

    /**
     * Get all questions with optional filters
     * @param topicId Optional topic filter
     * @param difficultyId Optional difficulty filter
     * @param statusId Optional status filter
     * @return List of questions
     */
    suspend fun getAllQuestions(
        topicId: Int? = null,
        difficultyId: Int? = null,
        statusId: Int? = null
    ): Result<List<Question>>

    /**
     * Get question by ID
     * @param questionId Question ID
     * @return Question with answers
     */
    suspend fun getQuestionById(questionId: Int): Result<Question>

    /**
     * Create new question (admin only)
     * @param question Question entity
     * @return Created question
     */
    suspend fun createQuestion(question: Question): Result<Question>

    /**
     * Update question (admin only)
     * @param question Question entity with updates
     * @return Updated question
     */
    suspend fun updateQuestion(question: Question): Result<Question>

    /**
     * Delete question (admin only)
     * @param questionId Question ID to delete
     * @return Deleted question
     */
    suspend fun deleteQuestion(questionId: Int): Result<Question>
}
