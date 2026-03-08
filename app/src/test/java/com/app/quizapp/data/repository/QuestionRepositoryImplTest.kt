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
    fun `getAllQuestions without filters returns success when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "questionId": 1,
                    "questionText": "What is 2+2?",
                    "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00",
                    "topic": {"topicId": 1, "topic": "Math"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 1, "mode": "Easy"}
                },
                {
                    "questionId": 2,
                    "questionText": "What is the capital of France?",
                    "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00",
                    "topic": {"topicId": 2, "topic": "Geography"},
                    "status": {"statusId": 1, "text": "Active"},
                    "difficulty": {"difficultyId": 2, "mode": "Medium"}
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val questions = (result as Result.Success).data
        assertThat(questions).hasSize(2)
        assertThat(questions[0].questionText).isEqualTo("What is 2+2?")
        assertThat(questions[1].questionText).isEqualTo("What is the capital of France?")
    }

    @Test
    fun `getAllQuestions with topicId filter sends correct query parameter`() = runTest {
        val jsonResponse = """[{"questionId": 1, "questionText": "Test", "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00", "topic": {"topicId": 5, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}]"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.getAllQuestions(topicId = 5)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("topicId=5")
    }

    @Test
    fun `getAllQuestions returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = repository.getAllQuestions()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getQuestionById returns success when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "questionId": 10,
                "questionText": "What is the speed of light?",
                "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00",
                "topic": {"topicId": 3, "topic": "Physics"},
                "status": {"statusId": 1, "text": "Active"},
                "difficulty": {"difficultyId": 3, "mode": "Hard"}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getQuestionById(10)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val question = (result as Result.Success).data
        assertThat(question.questionId).isEqualTo(10)
        assertThat(question.questionText).isEqualTo("What is the speed of light?")
        assertThat(question.difficulty.mode).isEqualTo("Hard")
    }

    @Test
    fun `getQuestionById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Question not found"))

        val result = repository.getQuestionById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `deleteQuestion returns success when API responds 200`() = runTest {
        val jsonResponse = """{"questionId": 5, "questionText": "Deleted", "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00", "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.deleteQuestion(5)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedQuestion = (result as Result.Success).data
        assertThat(deletedQuestion.questionId).isEqualTo(5)
    }

    @Test
    fun `deleteQuestion verifies correct endpoint is called`() = runTest {
        val jsonResponse = """{"questionId": 42, "questionText": "Test", "reviewedBy": null,
                    "createdAt": "2024-01-01T10:00:00", "topic": {"topicId": 1, "topic": "Test"}, "status": {"statusId": 1, "text": "Active"}, "difficulty": {"difficultyId": 1, "mode": "Easy"}}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.deleteQuestion(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/42")
        assertThat(request.method).isEqualTo("DELETE")
    }
}
