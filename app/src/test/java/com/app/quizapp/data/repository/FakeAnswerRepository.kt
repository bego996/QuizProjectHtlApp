package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Answer
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of AnswerRepository for testing purposes.
 */
class FakeAnswerRepository : AnswerRepository {

    private val testTopic = Topic(topicId = 1, topic = "Mathematics")
    private val testStatus = Status(statusId = 1, text = "Active")
    private val testDifficulty = Difficulty(difficultyId = 1, mode = "Easy")
    private val testQuestion = Question(
        questionId = 1,
        questionText = "What is 2 + 2?",
        reviewedBy = 1,
        topic = testTopic,
        status = testStatus,
        difficulty = testDifficulty
    )

    private val testAnswers = listOf(
        Answer(
            answerId = 1,
            text = "4",
            correct = true,
            question = testQuestion
        ),
        Answer(
            answerId = 2,
            text = "5",
            correct = false,
            question = testQuestion
        )
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var answersToReturn = testAnswers

    override suspend fun getAllAnswers(): Result<List<Answer>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(answersToReturn)
        }
    }

    override suspend fun geAnswerById(answerId: Int): Result<Answer> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val answer = answersToReturn.find { it.answerId == answerId }
            if (answer != null) {
                Result.Success(answer)
            } else {
                Result.Error("Answer with id $answerId not found")
            }
        }
    }
}
