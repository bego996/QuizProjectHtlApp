package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserQuestionApiService
import com.app.quizapp.domain.util.Result
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Integration test for UserQuestionRepositoryImpl using MockWebServer.
 */
class UserQuestionRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: UserQuestionApiService
    private lateinit var repository: UserQuestionRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(UserQuestionApiService::class.java)
        repository = UserQuestionRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllUserQuestions returns success when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "userQuestionId": 1,
                    "score": 100,
                    "user": {"userId": 1, "firstname": "John", "surname": "Doe", "birthdate": "2000-01-01", "nickname": "johnd", "email": "john@test.com", "password": "***", "userRole": {"userRoleId": 1, "userRole": "ROLE_USER"}},
                    "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}},
                    "answer": {"answerId": 1, "text": "Answer", "correct": true, "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}}
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestions = (result as Result.Success).data
        assertThat(userQuestions).hasSize(1)
        assertThat(userQuestions[0].score).isEqualTo(100)
    }

    @Test
    fun `getAllUserQuestions returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getUserQuestionById returns success when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "userQuestionId": 5,
                "score": 75,
                "user": {"userId": 2, "firstname": "Jane", "surname": "Smith", "birthdate": "1999-05-15", "nickname": "janes", "email": "jane@test.com", "password": "***", "userRole": {"userRoleId": 1, "userRole": "ROLE_USER"}},
                "question": {"questionId": 2, "questionText": "Question?", "reviewedBy": null, "topic": {"topicId": 2, "topic": "Science"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 2, "mode": "Medium"}},
                "answer": {"answerId": 5, "text": "Correct Answer", "correct": true, "question": {"questionId": 2, "questionText": "Question?", "reviewedBy": null, "topic": {"topicId": 2, "topic": "Science"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 2, "mode": "Medium"}}}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getUserQuestionById(5)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestion = (result as Result.Success).data
        assertThat(userQuestion.userQuestionId).isEqualTo(5)
        assertThat(userQuestion.score).isEqualTo(75)
        assertThat(userQuestion.user.firstname).isEqualTo("Jane")
    }

    @Test
    fun `getUserQuestionById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("UserQuestion not found"))

        val result = repository.getUserQuestionById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `deleteUserQuestion returns success when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "userQuestionId": 10,
                "score": 0,
                "user": {"userId": 1, "firstname": "Test", "surname": "User", "birthdate": "2000-01-01", "nickname": "test", "email": "test@test.com", "password": "***", "userRole": {"userRoleId": 1, "userRole": "ROLE_USER"}},
                "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}},
                "answer": {"answerId": 1, "text": "Wrong", "correct": false, "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.deleteUserQuestion(10)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedUserQuestion = (result as Result.Success).data
        assertThat(deletedUserQuestion.userQuestionId).isEqualTo(10)
    }

    @Test
    fun `deleteUserQuestion verifies correct endpoint is called`() = runTest {
        val jsonResponse = """{"userQuestionId": 42, "score": 100, "user": {"userId": 1, "firstname": "Test", "surname": "User", "birthdate": "2000-01-01", "nickname": "test", "email": "test@test.com", "password": "***", "userRole": {"userRoleId": 1, "userRole": "ROLE_USER"}}, "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}, "answer": {"answerId": 1, "text": "Test", "correct": true, "question": {"questionId": 1, "questionText": "Test?", "reviewedBy": null, "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}}}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.deleteUserQuestion(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/42")
        assertThat(request.method).isEqualTo("DELETE")
    }
}
