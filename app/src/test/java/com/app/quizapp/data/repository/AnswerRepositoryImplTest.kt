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
 * Tests Repository + ApiService + Retrofit + DTO mapping together.
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

    // ========== getAllAnswers Tests ==========

    @Test
    fun `getAllAnswers returns success with answers list when API responds 200`() = runTest {
        // Given: Mock successful API response with multiple answers
        val jsonResponse = """
            [
                {
                    "answerId": 1,
                    "text": "Berlin",
                    "correct": true,
                    "question": {
                        "questionId": 1,
                        "questionText": "What is the capital of Germany?",
                        "reviewedBy": null,
                        "topic": {"topicId": 1, "topic": "Geography"},
                        "status": {"statusId": 1, "text": "Active"},
                        "difficulty": {"difficultyId": 1, "mode": "Easy"}
                    }
                },
                {
                    "answerId": 2,
                    "text": "Munich",
                    "correct": false,
                    "question": {
                        "questionId": 1,
                        "questionText": "What is the capital of Germany?",
                        "reviewedBy": null,
                        "topic": {"topicId": 1, "topic": "Geography"},
                        "status": {"statusId": 1, "text": "Active"},
                        "difficulty": {"difficultyId": 1, "mode": "Easy"}
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getAllAnswers
        val result = repository.getAllAnswers()

        // Then: Success with parsed domain models
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answers = (result as Result.Success).data
        assertThat(answers).hasSize(2)
        assertThat(answers[0].text).isEqualTo("Berlin")
        assertThat(answers[0].correct).isTrue()
        assertThat(answers[1].text).isEqualTo("Munich")
        assertThat(answers[1].correct).isFalse()
    }

    @Test
    fun `getAllAnswers returns error when API responds with 500`() = runTest {
        // Given: Mock error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls getAllAnswers
        val result = repository.getAllAnswers()

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllAnswers returns empty list when API returns empty array`() = runTest {
        // Given: Mock empty response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When: Repository calls getAllAnswers
        val result = repository.getAllAnswers()

        // Then: Success with empty list
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answers = (result as Result.Success).data
        assertThat(answers).isEmpty()
    }

    // ========== getAllAnswersByQuestionId Tests ==========

    @Test
    fun `getAllAnswersByQuestionId returns success with answers when API responds 200`() = runTest {
        // Given: Mock successful API response
        val jsonResponse = """
            [
                {
                    "answerId": 10,
                    "text": "Paris",
                    "correct": true,
                    "question": {
                        "questionId": 5,
                        "questionText": "What is the capital of France?",
                        "reviewedBy": null,
                        "topic": {"topicId": 1, "topic": "Geography"},
                        "status": {"statusId": 1, "text": "Active"},
                        "difficulty": {"difficultyId": 1, "mode": "Easy"}
                    }
                },
                {
                    "answerId": 11,
                    "text": "Lyon",
                    "correct": false,
                    "question": {
                        "questionId": 5,
                        "questionText": "What is the capital of France?",
                        "reviewedBy": null,
                        "topic": {"topicId": 1, "topic": "Geography"},
                        "status": {"statusId": 1, "text": "Active"},
                        "difficulty": {"difficultyId": 1, "mode": "Easy"}
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getAllAnswersByQuestionId
        val result = repository.getAllAnswersByQuestionId(5)

        // Then: Success with answers for question
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answers = (result as Result.Success).data
        assertThat(answers).hasSize(2)
        assertThat(answers[0].text).isEqualTo("Paris")
        assertThat(answers[0].correct).isTrue()
        assertThat(answers[0].question.questionId).isEqualTo(5)
    }

    @Test
    fun `getAllAnswersByQuestionId verifies correct endpoint is called`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            [
                {
                    "answerId": 1,
                    "text": "Test",
                    "correct": true,
                    "question": {
                        "questionId": 42,
                        "questionText": "Test?",
                        "reviewedBy": null,
                        "topic": {"topicId": 1, "topic": "Test"},
                        "status": {"statusId": 1, "text": "Active"},
                        "difficulty": {"difficultyId": 1, "mode": "Easy"}
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getAllAnswersByQuestionId with specific questionId
        repository.getAllAnswersByQuestionId(42)

        // Then: Verify correct endpoint was called
        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("42")
        assertThat(request.method).isEqualTo("GET")
    }

    // ========== getAnswerById Tests ==========

    @Test
    fun `getAnswerById returns success with answer when API responds 200`() = runTest {
        // Given: Mock successful API response
        val jsonResponse = """
            {
                "answerId": 123,
                "text": "Rome",
                "correct": true,
                "question": {
                    "questionId": 10,
                    "questionText": "What is the capital of Italy?",
                    "reviewedBy": null,
                    "topic": {"topicId": 1, "topic": "Geography"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 2, "mode": "Medium"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getAnswerById
        val result = repository.getAnswerById(123)

        // Then: Success with parsed answer
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val answer = (result as Result.Success).data
        assertThat(answer.answerId).isEqualTo(123)
        assertThat(answer.text).isEqualTo("Rome")
        assertThat(answer.correct).isTrue()
        assertThat(answer.question.questionText).isEqualTo("What is the capital of Italy?")
    }

    @Test
    fun `getAnswerById returns error when API responds with 404`() = runTest {
        // Given: Mock 404 response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Answer not found")
        )

        // When: Repository calls getAnswerById
        val result = repository.getAnswerById(999)

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    // ========== createAnswer Tests ==========

    @Test
    fun `createAnswer returns success with created answer when API responds 200`() = runTest {
        // Given: Mock successful creation response
        val jsonResponse = """
            {
                "answerId": 200,
                "text": "Madrid",
                "correct": true,
                "question": {
                    "questionId": 15,
                    "questionText": "What is the capital of Spain?",
                    "reviewedBy": null,
                    "topic": {"topicId": 1, "topic": "Geography"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 1, "mode": "Easy"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // Create a test Answer (domain model)
        val testAnswer = createTestAnswer(
            answerId = 0,
            text = "Madrid",
            correct = true,
            questionId = 15
        )

        // When: Repository calls createAnswer
        val result = repository.createAnswer(testAnswer)

        // Then: Success with created answer
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val createdAnswer = (result as Result.Success).data
        assertThat(createdAnswer.answerId).isEqualTo(200)
        assertThat(createdAnswer.text).isEqualTo("Madrid")
        assertThat(createdAnswer.correct).isTrue()
    }

    @Test
    fun `createAnswer sends correct request body to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "answerId": 1,
                "text": "Test",
                "correct": true,
                "question": {
                    "questionId": 1,
                    "questionText": "Test?",
                    "reviewedBy": null,
                    "topic": {"topicId": 1, "topic": "Test"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 1, "mode": "Easy"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val testAnswer = createTestAnswer(
            answerId = 0,
            text = "Test Answer",
            correct = false,
            questionId = 1
        )

        // When: Repository calls createAnswer
        repository.createAnswer(testAnswer)

        // Then: Verify request body
        val request = mockWebServer.takeRequest()
        assertThat(request.method).isEqualTo("POST")
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"text\":\"Test Answer\"")
        assertThat(requestBody).contains("\"correct\":false")
    }

    // ========== updateAnswer Tests ==========

    @Test
    fun `updateAnswer returns success with updated answer when API responds 200`() = runTest {
        // Given: Mock successful update response
        val jsonResponse = """
            {
                "answerId": 50,
                "text": "Updated Answer",
                "correct": false,
                "question": {
                    "questionId": 20,
                    "questionText": "Sample question?",
                    "reviewedBy": null,
                    "topic": {"topicId": 2, "topic": "Science"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 2, "mode": "Medium"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val testAnswer = createTestAnswer(
            answerId = 50,
            text = "Updated Answer",
            correct = false,
            questionId = 20
        )

        // When: Repository calls updateAnswer
        val result = repository.updateAnswer(testAnswer)

        // Then: Success with updated answer
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedAnswer = (result as Result.Success).data
        assertThat(updatedAnswer.answerId).isEqualTo(50)
        assertThat(updatedAnswer.text).isEqualTo("Updated Answer")
        assertThat(updatedAnswer.correct).isFalse()
    }

    @Test
    fun `updateAnswer returns error when API responds with 404`() = runTest {
        // Given: Mock 404 response (answer not found)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Answer not found")
        )

        val testAnswer = createTestAnswer(
            answerId = 999,
            text = "Non-existent",
            correct = true,
            questionId = 1
        )

        // When: Repository calls updateAnswer
        val result = repository.updateAnswer(testAnswer)

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // ========== deleteAnswer Tests ==========

    @Test
    fun `deleteAnswer returns success with deleted answer when API responds 200`() = runTest {
        // Given: Mock successful deletion response
        val jsonResponse = """
            {
                "answerId": 100,
                "text": "Deleted Answer",
                "correct": true,
                "question": {
                    "questionId": 30,
                    "questionText": "Question to delete?",
                    "reviewedBy": null,
                    "topic": {"topicId": 3, "topic": "History"},
                    "status": {"statusId": 2, "text": "Inactive"},
                    "difficulty": {"difficultyId": 3, "mode": "Hard"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls deleteAnswer
        val result = repository.deleteAnswer(100)

        // Then: Success with deleted answer
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedAnswer = (result as Result.Success).data
        assertThat(deletedAnswer.answerId).isEqualTo(100)
        assertThat(deletedAnswer.text).isEqualTo("Deleted Answer")
    }

    @Test
    fun `deleteAnswer verifies correct endpoint is called`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "answerId": 42,
                "text": "Test",
                "correct": true,
                "question": {
                    "questionId": 1,
                    "questionText": "Test?",
                    "reviewedBy": null,
                    "topic": {"topicId": 1, "topic": "Test"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 1, "mode": "Easy"}
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls deleteAnswer with specific id
        repository.deleteAnswer(42)

        // Then: Verify correct endpoint was called
        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/42")
        assertThat(request.method).isEqualTo("DELETE")
    }

    @Test
    fun `deleteAnswer returns error when API responds with 500`() = runTest {
        // Given: Mock server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls deleteAnswer
        val result = repository.deleteAnswer(1)

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // ========== Helper Functions ==========

    /**
     * Helper function to create test Answer domain model
     */
    private fun createTestAnswer(
        answerId: Int,
        text: String,
        correct: Boolean,
        questionId: Int
    ): com.app.quizapp.domain.model.Answer {
        return com.app.quizapp.domain.model.Answer(
            answerId = answerId,
            text = text,
            correct = correct,
            question = com.app.quizapp.domain.model.Question(
                questionId = questionId,
                questionText = "Sample question?",
                reviewedBy = 0,
                topic = com.app.quizapp.domain.model.Topic(
                    topicId = 1,
                    topic = "Test Topic"
                ),
                status = com.app.quizapp.domain.model.Status(
                    statusId = 1,
                    text = "Active"
                ),
                difficulty = com.app.quizapp.domain.model.Difficulty(
                    difficultyId = 1,
                    mode = "Easy"
                )
            )
        )
    }
}
