package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.QuestionApiService
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
 * Integration test for QuestionRepositoryImpl using MockWebServer.
 */
class QuestionRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: QuestionApiService
    private lateinit var repository: QuestionRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(QuestionApiService::class.java)
        repository = QuestionRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllQuestions returns success with questions list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
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
                {
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
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val questions = (result as Result.Success).data
        assertThat(questions).hasSize(2)
        assertThat(questions[0].questionText).isEqualTo("What is 2 + 2?")
        assertThat(questions[0].topic.topic).isEqualTo("Mathematics")
        assertThat(questions[0].status.text).isEqualTo("Active")
        assertThat(questions[0].difficulty.mode).isEqualTo("Easy")
        assertThat(questions[1].questionText).isEqualTo("What is the capital of France?")
    }

    @Test
    fun `getAllQuestions returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllQuestions returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllQuestions returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val questions = (result as Result.Success).data
        assertThat(questions).isEmpty()
    }

    @Test
    fun `getQuestionById returns success with question when API responds 200`() = runTest {
        val jsonResponse = """
            {
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
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getQuestionById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val question = (result as Result.Success).data
        assertThat(question.questionId).isEqualTo(1)
        assertThat(question.questionText).isEqualTo("What is 2 + 2?")
        assertThat(question.reviewedBy).isEqualTo(1)
        assertThat(question.topic.topic).isEqualTo("Mathematics")
        assertThat(question.status.text).isEqualTo("Active")
        assertThat(question.difficulty.mode).isEqualTo("Easy")
    }

    @Test
    fun `getQuestionById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Question not found")
        )

        val result = repository.getQuestionById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getQuestionById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "questionId": 42,
                "questionText": "Test question",
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
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getQuestionById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/questions/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "questionId": 123,
                "questionText": "Complex test question?",
                "reviewedBy": 99,
                "topic": {
                    "topicId": 5,
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
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getQuestionById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val question = (result as Result.Success).data
        assertThat(question.questionId).isEqualTo(123)
        assertThat(question.questionText).isEqualTo("Complex test question?")
        assertThat(question.reviewedBy).isEqualTo(99)
        assertThat(question.topic.topicId).isEqualTo(5)
        assertThat(question.topic.topic).isEqualTo("Physics")
        assertThat(question.status.statusId).isEqualTo(2)
        assertThat(question.status.text).isEqualTo("Pending")
        assertThat(question.difficulty.difficultyId).isEqualTo(3)
        assertThat(question.difficulty.mode).isEqualTo("Hard")
    }
}
