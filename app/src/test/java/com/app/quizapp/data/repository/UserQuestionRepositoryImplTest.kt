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
    fun `getAllUserQuestions returns success with userQuestions list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "userQuestionId": 1,
                    "user": {
                        "userId": 1,
                        "surname": "Mustermann",
                        "firstname": "Max",
                        "birthdate": "2000-01-01",
                        "nickname": "maxm",
                        "email": "max@test.com",
                        "password": "test123",
                        "userRole": {
                            "userRoleId": 1,
                            "userRole": "Student"
                        }
                    },
                    "question": {
                        "questionId": 1,
                        "questionText": "What is 2 + 2?",
                        "reviewedBy": 1,
                        "topic": {
                            "topicId": 1,
                            "topic": "Mathematics"
                        },
                        "status": {
                            "statusId": 1,
                            "text": "Active"
                        },
                        "difficulty": {
                            "difficultyId": 1,
                            "mode": "Easy"
                        }
                    },
                    "score": 100
                },
                {
                    "userQuestionId": 2,
                    "user": {
                        "userId": 1,
                        "surname": "Mustermann",
                        "firstname": "Max",
                        "birthdate": "2000-01-01",
                        "nickname": "maxm",
                        "email": "max@test.com",
                        "password": "test123",
                        "userRole": {
                            "userRoleId": 1,
                            "userRole": "Student"
                        }
                    },
                    "question": {
                        "questionId": 2,
                        "questionText": "What is the capital of France?",
                        "reviewedBy": 1,
                        "topic": {
                            "topicId": 2,
                            "topic": "Geography"
                        },
                        "status": {
                            "statusId": 1,
                            "text": "Active"
                        },
                        "difficulty": {
                            "difficultyId": 2,
                            "mode": "Medium"
                        }
                    },
                    "score": 85
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestions = (result as Result.Success).data
        assertThat(userQuestions).hasSize(2)
        assertThat(userQuestions[0].score).isEqualTo(100)
        assertThat(userQuestions[0].user.firstname).isEqualTo("Max")
        assertThat(userQuestions[0].question.questionText).isEqualTo("What is 2 + 2?")
        assertThat(userQuestions[1].score).isEqualTo(85)
    }

    @Test
    fun `getAllUserQuestions returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllUserQuestions returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllUserQuestions returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllUserQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestions = (result as Result.Success).data
        assertThat(userQuestions).isEmpty()
    }

    @Test
    fun `getUserQuestionById returns success with userQuestion when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "userQuestionId": 1,
                "user": {
                    "userId": 1,
                    "surname": "Mustermann",
                    "firstname": "Max",
                    "birthdate": "2000-01-01",
                    "nickname": "maxm",
                    "email": "max@test.com",
                    "password": "test123",
                    "userRole": {
                        "userRoleId": 1,
                        "userRole": "Student"
                    }
                },
                "question": {
                    "questionId": 1,
                    "questionText": "What is 2 + 2?",
                    "reviewedBy": 1,
                    "topic": {
                        "topicId": 1,
                        "topic": "Mathematics"
                    },
                    "status": {
                        "statusId": 1,
                        "text": "Active"
                    },
                    "difficulty": {
                        "difficultyId": 1,
                        "mode": "Easy"
                    }
                },
                "score": 100
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getUserQuestionById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestion = (result as Result.Success).data
        assertThat(userQuestion.userQuestionId).isEqualTo(1)
        assertThat(userQuestion.score).isEqualTo(100)
        assertThat(userQuestion.user.firstname).isEqualTo("Max")
        assertThat(userQuestion.question.questionText).isEqualTo("What is 2 + 2?")
    }

    @Test
    fun `getUserQuestionById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("UserQuestion not found")
        )

        val result = repository.getUserQuestionById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getUserQuestionById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "userQuestionId": 42,
                "user": {
                    "userId": 1,
                    "surname": "Test",
                    "firstname": "User",
                    "birthdate": "2000-01-01",
                    "nickname": "testuser",
                    "email": "test@test.com",
                    "password": "test",
                    "userRole": {
                        "userRoleId": 1,
                        "userRole": "Student"
                    }
                },
                "question": {
                    "questionId": 1,
                    "questionText": "Test question?",
                    "reviewedBy": 1,
                    "topic": {
                        "topicId": 1,
                        "topic": "Test"
                    },
                    "status": {
                        "statusId": 1,
                        "text": "Active"
                    },
                    "difficulty": {
                        "difficultyId": 1,
                        "mode": "Easy"
                    }
                },
                "score": 50
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getUserQuestionById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/userQuestions/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "userQuestionId": 123,
                "user": {
                    "userId": 99,
                    "surname": "Schmidt",
                    "firstname": "Anna",
                    "birthdate": "1998-05-20",
                    "nickname": "annas",
                    "email": "anna@test.com",
                    "password": "pass123",
                    "userRole": {
                        "userRoleId": 2,
                        "userRole": "Teacher"
                    }
                },
                "question": {
                    "questionId": 88,
                    "questionText": "Complex question text?",
                    "reviewedBy": 5,
                    "topic": {
                        "topicId": 3,
                        "topic": "Physics"
                    },
                    "status": {
                        "statusId": 2,
                        "text": "Pending"
                    },
                    "difficulty": {
                        "difficultyId": 3,
                        "mode": "Hard"
                    }
                },
                "score": 95
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getUserQuestionById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userQuestion = (result as Result.Success).data
        assertThat(userQuestion.userQuestionId).isEqualTo(123)
        assertThat(userQuestion.score).isEqualTo(95)
        assertThat(userQuestion.user.userId).isEqualTo(99)
        assertThat(userQuestion.user.firstname).isEqualTo("Anna")
        assertThat(userQuestion.user.surname).isEqualTo("Schmidt")
        assertThat(userQuestion.user.userRole.userRole).isEqualTo("Teacher")
        assertThat(userQuestion.question.questionId).isEqualTo(88)
        assertThat(userQuestion.question.questionText).isEqualTo("Complex question text?")
        assertThat(userQuestion.question.topic.topic).isEqualTo("Physics")
        assertThat(userQuestion.question.difficulty.mode).isEqualTo("Hard")
    }
}
