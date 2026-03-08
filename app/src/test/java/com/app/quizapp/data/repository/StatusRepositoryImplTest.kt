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
    fun `getAllStatuses returns success when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {"statusId": 1, "text": "Active"},
                {"statusId": 2, "text": "Inactive"},
                {"statusId": 3, "text": "Pending"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val statuses = (result as Result.Success).data
        assertThat(statuses).hasSize(3)
        assertThat(statuses[0].text).isEqualTo("Active")
        assertThat(statuses[1].text).isEqualTo("Inactive")
    }

    @Test
    fun `getAllStatuses returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = repository.getAllStatuses()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getStatusById returns success when API responds 200`() = runTest {
        val jsonResponse = """{"statusId": 2, "text": "Inactive"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getStatusById(2)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val status = (result as Result.Success).data
        assertThat(status.statusId).isEqualTo(2)
        assertThat(status.text).isEqualTo("Inactive")
    }

    @Test
    fun `getStatusById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Status not found"))

        val result = repository.getStatusById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `deleteStatus returns success when API responds 200`() = runTest {
        val jsonResponse = """{"statusId": 5, "text": "Archived"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.deleteStatus(5)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedStatus = (result as Result.Success).data
        assertThat(deletedStatus.statusId).isEqualTo(5)
        assertThat(deletedStatus.text).isEqualTo("Archived")
    }

    @Test
    fun `deleteStatus verifies correct endpoint is called`() = runTest {
        val jsonResponse = """{"statusId": 10, "text": "Test"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.deleteStatus(10)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/10")
        assertThat(request.method).isEqualTo("DELETE")
    }
}
