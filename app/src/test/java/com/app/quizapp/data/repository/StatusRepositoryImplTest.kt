package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.StatusApiService
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
 * Integration test for StatusRepositoryImpl using MockWebServer.
 */
class StatusRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StatusApiService
    private lateinit var repository: StatusRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(StatusApiService::class.java)
        repository = StatusRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllStatuses returns success with statuses list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "statusId": 1,
                    "text": "Active"
                },
                {
                    "statusId": 2,
                    "text": "Pending"
                },
                {
                    "statusId": 3,
                    "text": "Inactive"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val statuses = (result as Result.Success).data
        assertThat(statuses).hasSize(3)
        assertThat(statuses[0].text).isEqualTo("Active")
        assertThat(statuses[1].text).isEqualTo("Pending")
        assertThat(statuses[2].text).isEqualTo("Inactive")
    }

    @Test
    fun `getAllStatuses returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllStatuses returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllStatuses returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val statuses = (result as Result.Success).data
        assertThat(statuses).isEmpty()
    }

    @Test
    fun `getStatusById returns success with status when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "statusId": 1,
                "text": "Approved"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getStatusById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val status = (result as Result.Success).data
        assertThat(status.statusId).isEqualTo(1)
        assertThat(status.text).isEqualTo("Approved")
    }

    @Test
    fun `getStatusById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Status not found")
        )

        val result = repository.getStatusById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getStatusById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "statusId": 42,
                "text": "Test Status"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getStatusById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/status/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "statusId": 123,
                "text": "Under Review"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getStatusById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val status = (result as Result.Success).data
        assertThat(status.statusId).isEqualTo(123)
        assertThat(status.text).isEqualTo("Under Review")
    }
}
