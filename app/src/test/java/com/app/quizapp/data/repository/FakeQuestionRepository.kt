package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of QuestionRepository for testing purposes.
 */
class FakeQuestionRepository : QuestionRepository {

    private val testTopic = Topic(topicId = 1, topic = "Mathematics")
    private val testStatus = Status(statusId = 1, text = "Active")
    private val testDifficulty = Difficulty(difficultyId = 1, mode = "Easy")

    private val testQuestions = listOf(
        Question(
            questionId = 1,
            questionText = "What is 2 + 2?",
            reviewedBy = 1,
            topic = testTopic,
            status = testStatus,
            difficulty = testDifficulty
        ),
        Question(
            questionId = 2,
            questionText = "What is the capital of France?",
            reviewedBy = 1,
            topic = testTopic,
            status = testStatus,
            difficulty = testDifficulty
        )
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var questionsToReturn = testQuestions

    override suspend fun getAllQuestions(): Result<List<Question>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(questionsToReturn)
        }
    }

    override suspend fun getQuestionById(id: Int): Result<Question> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val question = questionsToReturn.find { it.questionId == id }
            if (question != null) {
                Result.Success(question)
            } else {
                Result.Error("Question with id $id not found")
            }
        }
    }
}
