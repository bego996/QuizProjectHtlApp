package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.LlmApiService
import com.app.quizapp.domain.util.Result
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Integration test for LlmRepositoryImpl using MockWebServer.
 * Tests AI/LLM quiz generation and database operations.
 */
class LlmRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: LlmApiService
    private lateinit var repository: LlmRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(LlmApiService::class.java)
        repository = LlmRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ========== Generate Quiz Tests ==========

    @Test
    fun `generateQuiz returns success with quiz data when API responds 200`() = runTest {
        // Given: Mock successful quiz generation response
        val jsonResponse = """
            {
                "model": "llama2",
                "created_at": "2024-01-15T10:30:00Z",
                "quiz": {
                    "topic": "Mathematics",
                    "question": "What is 2 + 2?",
                    "difficulty": "Easy",
                    "answers": ["3", "4", "5", "6"],
                    "correct_answer": 1
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls generateQuiz
        val result = repository.generateQuiz(
            model = "llama2",
            prompt = "Generate a math quiz question",
            stream = false,
            format = null
        )

        // Then: Success with quiz response
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val quizResponse = (result as Result.Success).data
        assertThat(quizResponse.model).isEqualTo("llama2")
        assertThat(quizResponse.createdAt).isEqualTo("2024-01-15T10:30:00Z")
        assertThat(quizResponse.quiz.topic).isEqualTo("Mathematics")
        assertThat(quizResponse.quiz.question).isEqualTo("What is 2 + 2?")
        assertThat(quizResponse.quiz.difficulty).isEqualTo("Easy")
        assertThat(quizResponse.quiz.answers).hasSize(4)
        assertThat(quizResponse.quiz.answers[1]).isEqualTo("4")
        assertThat(quizResponse.quiz.correctAnswer).isEqualTo(1)
    }

    @Test
    fun `generateQuiz sends correct request body to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "model": "llama2",
                "created_at": "2024-01-15T10:30:00Z",
                "quiz": {
                    "topic": "Science",
                    "question": "Test question",
                    "difficulty": "Medium",
                    "answers": ["A", "B", "C", "D"],
                    "correct_answer": 0
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls generateQuiz with specific parameters
        repository.generateQuiz(
            model = "llama2",
            prompt = "Generate a science quiz",
            stream = false,
            format = null
        )

        // Then: Verify correct endpoint and request body
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/api/llm/quiz/generate")
        assertThat(request.method).isEqualTo("POST")
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"model\":\"llama2\"")
        assertThat(requestBody).contains("\"prompt\":\"Generate a science quiz\"")
        assertThat(requestBody).contains("\"stream\":false")
    }

    @Test
    fun `generateQuiz with stream enabled sends correct parameter`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "model": "llama2",
                "created_at": "2024-01-15T10:30:00Z",
                "quiz": {
                    "topic": "History",
                    "question": "Test question",
                    "difficulty": "Hard",
                    "answers": ["A", "B", "C", "D"],
                    "correct_answer": 2
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls generateQuiz with stream enabled
        repository.generateQuiz(
            model = "llama2",
            prompt = "Generate a history quiz",
            stream = true,
            format = null
        )

        // Then: Verify stream parameter is true
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"stream\":true")
    }

    @Test
    fun `generateQuiz with format sends JSON format to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "model": "llama2",
                "created_at": "2024-01-15T10:30:00Z",
                "quiz": {
                    "topic": "Geography",
                    "question": "Test question",
                    "difficulty": "Medium",
                    "answers": ["A", "B", "C", "D"],
                    "correct_answer": 3
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // Create a sample JSON format
        val format = JsonObject().apply {
            addProperty("type", "quiz")
        }

        // When: Repository calls generateQuiz with format
        repository.generateQuiz(
            model = "llama2",
            prompt = "Generate a geography quiz",
            stream = false,
            format = format
        )

        // Then: Verify format is included in request
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"format\"")
    }

    @Test
    fun `generateQuiz returns error when API responds with 500`() = runTest {
        // Given: Mock server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls generateQuiz
        val result = repository.generateQuiz(
            model = "llama2",
            prompt = "Generate quiz",
            stream = false,
            format = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `generateQuiz returns error when API responds with 403 Forbidden`() = runTest {
        // Given: Mock forbidden response (non-admin user)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(403)
                .setBody("Access denied - Admin role required")
        )

        // When: Repository calls generateQuiz
        val result = repository.generateQuiz(
            model = "llama2",
            prompt = "Generate quiz",
            stream = false,
            format = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `generateQuiz returns error when network fails`() = runTest {
        // Given: Server shutdown (simulates network error)
        mockWebServer.shutdown()

        // When: Repository calls generateQuiz
        val result = repository.generateQuiz(
            model = "llama2",
            prompt = "Generate quiz",
            stream = false,
            format = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // ========== Add Quiz to Database Tests ==========

    @Test
    fun `addQuizToDatabase returns success with saved quiz when API responds 200`() = runTest {
        // Given: Mock successful save response
        val jsonResponse = """
            {
                "topic": "Physics",
                "question": "What is the speed of light?",
                "difficulty": "Medium",
                "answers": ["299,792,458 m/s", "300,000,000 m/s", "299,000,000 m/s", "298,000,000 m/s"],
                "correct_answer": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls addQuizToDatabase
        val result = repository.addQuizToDatabase(
            topic = "Physics",
            question = "What is the speed of light?",
            difficulty = "Medium",
            answers = listOf("299,792,458 m/s", "300,000,000 m/s", "299,000,000 m/s", "298,000,000 m/s"),
            correctAnswer = 0
        )

        // Then: Success with saved quiz data
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val quizDto = (result as Result.Success).data
        assertThat(quizDto.topic).isEqualTo("Physics")
        assertThat(quizDto.question).isEqualTo("What is the speed of light?")
        assertThat(quizDto.difficulty).isEqualTo("Medium")
        assertThat(quizDto.answers).hasSize(4)
        assertThat(quizDto.answers[0]).isEqualTo("299,792,458 m/s")
        assertThat(quizDto.correctAnswer).isEqualTo(0)
    }

    @Test
    fun `addQuizToDatabase sends correct request body to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "topic": "Chemistry",
                "question": "What is H2O?",
                "difficulty": "Easy",
                "answers": ["Water", "Oxygen", "Hydrogen", "Peroxide"],
                "correct_answer": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls addQuizToDatabase
        repository.addQuizToDatabase(
            topic = "Chemistry",
            question = "What is H2O?",
            difficulty = "Easy",
            answers = listOf("Water", "Oxygen", "Hydrogen", "Peroxide"),
            correctAnswer = 0
        )

        // Then: Verify correct endpoint and request body
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/api/llm/quiz/add")
        assertThat(request.method).isEqualTo("POST")
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"topic\":\"Chemistry\"")
        assertThat(requestBody).contains("\"question\":\"What is H2O?\"")
        assertThat(requestBody).contains("\"difficulty\":\"Easy\"")
        assertThat(requestBody).contains("\"Water\"")
        assertThat(requestBody).contains("\"correct_answer\":0")
    }

    @Test
    fun `addQuizToDatabase with different correct answer index works correctly`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "topic": "Biology",
                "question": "What is the powerhouse of the cell?",
                "difficulty": "Easy",
                "answers": ["Nucleus", "Ribosome", "Mitochondria", "Chloroplast"],
                "correct_answer": 2
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls addQuizToDatabase with correct answer at index 2
        val result = repository.addQuizToDatabase(
            topic = "Biology",
            question = "What is the powerhouse of the cell?",
            difficulty = "Easy",
            answers = listOf("Nucleus", "Ribosome", "Mitochondria", "Chloroplast"),
            correctAnswer = 2
        )

        // Then: Correct answer index is preserved
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val quizDto = (result as Result.Success).data
        assertThat(quizDto.correctAnswer).isEqualTo(2)
        assertThat(quizDto.answers[2]).isEqualTo("Mitochondria")
    }

    @Test
    fun `addQuizToDatabase returns error when API responds with 400 Bad Request`() = runTest {
        // Given: Mock bad request response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("Invalid quiz data")
        )

        // When: Repository calls addQuizToDatabase
        val result = repository.addQuizToDatabase(
            topic = "Test",
            question = "Invalid question",
            difficulty = "Easy",
            answers = emptyList(),
            correctAnswer = 0
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `addQuizToDatabase returns error when API responds with 500`() = runTest {
        // Given: Mock server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls addQuizToDatabase
        val result = repository.addQuizToDatabase(
            topic = "Test",
            question = "Test question",
            difficulty = "Easy",
            answers = listOf("A", "B", "C", "D"),
            correctAnswer = 0
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `addQuizToDatabase returns error when network fails`() = runTest {
        // Given: Server shutdown (simulates network error)
        mockWebServer.shutdown()

        // When: Repository calls addQuizToDatabase
        val result = repository.addQuizToDatabase(
            topic = "Test",
            question = "Test question",
            difficulty = "Easy",
            answers = listOf("A", "B", "C", "D"),
            correctAnswer = 0
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `DTO mapping converts all quiz fields correctly`() = runTest {
        // Given: Mock response with all fields
        val jsonResponse = """
            {
                "topic": "Advanced Mathematics",
                "question": "What is the derivative of x^2?",
                "difficulty": "Hard",
                "answers": ["2x", "x", "x^2", "2"],
                "correct_answer": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls addQuizToDatabase
        val result = repository.addQuizToDatabase(
            topic = "Advanced Mathematics",
            question = "What is the derivative of x^2?",
            difficulty = "Hard",
            answers = listOf("2x", "x", "x^2", "2"),
            correctAnswer = 0
        )

        // Then: All fields correctly mapped from DTO
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val quizDto = (result as Result.Success).data
        assertThat(quizDto.topic).isEqualTo("Advanced Mathematics")
        assertThat(quizDto.question).isEqualTo("What is the derivative of x^2?")
        assertThat(quizDto.difficulty).isEqualTo("Hard")
        assertThat(quizDto.answers).containsExactly("2x", "x", "x^2", "2").inOrder()
        assertThat(quizDto.correctAnswer).isEqualTo(0)
    }
}
