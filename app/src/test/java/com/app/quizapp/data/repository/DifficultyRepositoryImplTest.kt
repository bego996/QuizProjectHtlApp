package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.DifficultyApiService
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
 * Integration test for DifficultyRepositoryImpl using MockWebServer.
 */
class DifficultyRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: DifficultyApiService
    private lateinit var repository: DifficultyRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(DifficultyApiService::class.java)
        repository = DifficultyRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllDifficulties returns success with difficulties list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "difficultyId": 1,
                    "mode": "Easy"
                },
                {
                    "difficultyId": 2,
                    "mode": "Medium"
                },
                {
                    "difficultyId": 3,
                    "mode": "Hard"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllDifficulties()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val difficulties = (result as Result.Success).data
        assertThat(difficulties).hasSize(3)
        assertThat(difficulties[0].mode).isEqualTo("Easy")
        assertThat(difficulties[1].mode).isEqualTo("Medium")
        assertThat(difficulties[2].mode).isEqualTo("Hard")
    }

    @Test
    fun `getAllDifficulties returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllDifficulties()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllDifficulties returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllDifficulties()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllDifficulties returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllDifficulties()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val difficulties = (result as Result.Success).data
        assertThat(difficulties).isEmpty()
    }

    @Test
    fun `getDifficultyById returns success with difficulty when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "difficultyId": 1,
                "mode": "Expert"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getDifficultyById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val difficulty = (result as Result.Success).data
        assertThat(difficulty.difficultyId).isEqualTo(1)
        assertThat(difficulty.mode).isEqualTo("Expert")
    }

    @Test
    fun `getDifficultyById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Difficulty not found")
        )

        val result = repository.getDifficultyById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getDifficultyById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "difficultyId": 42,
                "mode": "Test Mode"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getDifficultyById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/difficulties/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "difficultyId": 123,
                "mode": "Nightmare"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getDifficultyById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val difficulty = (result as Result.Success).data
        assertThat(difficulty.difficultyId).isEqualTo(123)
        assertThat(difficulty.mode).isEqualTo("Nightmare")
    }
}
