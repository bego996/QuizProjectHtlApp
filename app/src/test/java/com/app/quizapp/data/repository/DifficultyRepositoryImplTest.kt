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
    fun `getAllDifficulties returns success when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {"difficultyId": 1, "mode": "Easy"},
                {"difficultyId": 2, "mode": "Medium"},
                {"difficultyId": 3, "mode": "Hard"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

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
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = repository.getAllDifficulties()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getDifficultyById returns success when API responds 200`() = runTest {
        val jsonResponse = """{"difficultyId": 2, "mode": "Medium"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getDifficultyById(2)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val difficulty = (result as Result.Success).data
        assertThat(difficulty.difficultyId).isEqualTo(2)
        assertThat(difficulty.mode).isEqualTo("Medium")
    }

    @Test
    fun `getDifficultyById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Difficulty not found"))

        val result = repository.getDifficultyById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `deleteDifficulty returns success when API responds 200`() = runTest {
        val jsonResponse = """{"difficultyId": 5, "mode": "Expert"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.deleteDifficulty(5)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedDifficulty = (result as Result.Success).data
        assertThat(deletedDifficulty.difficultyId).isEqualTo(5)
    }

    @Test
    fun `deleteDifficulty verifies correct endpoint is called`() = runTest {
        val jsonResponse = """{"difficultyId": 10, "mode": "Test"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.deleteDifficulty(10)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/10")
        assertThat(request.method).isEqualTo("DELETE")
    }
}
