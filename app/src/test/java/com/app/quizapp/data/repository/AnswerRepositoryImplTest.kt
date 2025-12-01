package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.AnswerApiService
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
 * Integration test for AnswerRepositoryImpl using MockWebServer.
 */
class AnswerRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: AnswerApiService
    private lateinit var repository: AnswerRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(AnswerApiService::class.java)
        repository = AnswerRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllAnswers returns success with answers list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "answerId": 1,
                    "text": "4",
                    "correct": true,
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
                    }
                },
                {
                    "answerId": 2,
                    "text": "5",
                    "correct": false,
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
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllAnswers()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answers = (result as Result.Success).data
        assertThat(answers).hasSize(2)
        assertThat(answers[0].text).isEqualTo("4")
        assertThat(answers[0].correct).isTrue()
        assertThat(answers[0].question.questionText).isEqualTo("What is 2 + 2?")
        assertThat(answers[1].text).isEqualTo("5")
        assertThat(answers[1].correct).isFalse()
    }

    @Test
    fun `getAllAnswers returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllAnswers()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllAnswers returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllAnswers()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllAnswers returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllAnswers()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answers = (result as Result.Success).data
        assertThat(answers).isEmpty()
    }

    @Test
    fun `geAnswerById returns success with answer when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "answerId": 1,
                "text": "Paris",
                "correct": true,
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
                        "difficultyId": 1,
                        "mode": "Easy"
                    }
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.geAnswerById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answer = (result as Result.Success).data
        assertThat(answer.answerId).isEqualTo(1)
        assertThat(answer.text).isEqualTo("Paris")
        assertThat(answer.correct).isTrue()
        assertThat(answer.question.questionText).isEqualTo("What is the capital of France?")
    }

    @Test
    fun `geAnswerById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Answer not found")
        )

        val result = repository.geAnswerById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `geAnswerById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "answerId": 42,
                "text": "Test answer",
                "correct": true,
                "question": {
                    "questionId": 1,
                    "questionText": "Test?",
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
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.geAnswerById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/answers/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "answerId": 123,
                "text": "Correct answer text",
                "correct": false,
                "question": {
                    "questionId": 99,
                    "questionText": "Complex question?",
                    "reviewedBy": 5,
                    "topic": {
                        "topicId": 3,
                        "topic": "Science"
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
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.geAnswerById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answer = (result as Result.Success).data
        assertThat(answer.answerId).isEqualTo(123)
        assertThat(answer.text).isEqualTo("Correct answer text")
        assertThat(answer.correct).isFalse()
        assertThat(answer.question.questionId).isEqualTo(99)
        assertThat(answer.question.questionText).isEqualTo("Complex question?")
        assertThat(answer.question.reviewedBy).isEqualTo(5)
        assertThat(answer.question.topic.topicId).isEqualTo(3)
        assertThat(answer.question.topic.topic).isEqualTo("Science")
        assertThat(answer.question.status.statusId).isEqualTo(2)
        assertThat(answer.question.status.text).isEqualTo("Pending")
        assertThat(answer.question.difficulty.difficultyId).isEqualTo(3)
        assertThat(answer.question.difficulty.mode).isEqualTo("Hard")
    }
}
