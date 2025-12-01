package com.app.quizapp.data.repository

import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Question
import com.app.quizapp.domain.model.Status
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.domain.model.User
import com.app.quizapp.domain.model.UserQuestion
import com.app.quizapp.domain.model.UserRole
import com.app.quizapp.domain.repository.UserQuestionRepository
import com.app.quizapp.domain.util.Result

/**
 * Fake implementation of UserQuestionRepository for testing purposes.
 */
class FakeUserQuestionRepository : UserQuestionRepository {

    private val testUserRole = UserRole(userRoleId = 1, userRole = "Student")
    private val testUser = User(
        userId = 1,
        surname = "Mustermann",
        firstname = "Max",
        birthdate = "2000-01-01",
        nickname = "maxm",
        email = "max@test.com",
        password = "test123",
        userRole = testUserRole
    )
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

    private val testUserQuestions = listOf(
        UserQuestion(
            userQuestionId = 1,
            user = testUser,
            question = testQuestion,
            score = 100
        ),
        UserQuestion(
            userQuestionId = 2,
            user = testUser,
            question = testQuestion,
            score = 85
        )
    )

    var shouldReturnError = false
    var errorMessage = "Test error"
    var userQuestionsToReturn = testUserQuestions

    override suspend fun getAllUserQuestions(): Result<List<UserQuestion>> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            Result.Success(userQuestionsToReturn)
        }
    }

    override suspend fun getUserQuestionById(id: Int): Result<UserQuestion> {
        return if (shouldReturnError) {
            Result.Error(errorMessage)
        } else {
            val userQuestion = userQuestionsToReturn.find { it.userQuestionId == id }
            if (userQuestion != null) {
                Result.Success(userQuestion)
            } else {
                Result.Error("UserQuestion with id $id not found")
            }
        }
    }
}
