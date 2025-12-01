package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.TopicApiService
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
 * Integration test for TopicRepositoryImpl using MockWebServer.
 */
class TopicRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: TopicApiService
    private lateinit var repository: TopicRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(TopicApiService::class.java)
        repository = TopicRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllTopics returns success with topics list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "topicId": 1,
                    "topic": "Mathematics"
                },
                {
                    "topicId": 2,
                    "topic": "Geography"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllTopics()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val topics = (result as Result.Success).data
        assertThat(topics).hasSize(2)
        assertThat(topics[0].topic).isEqualTo("Mathematics")
        assertThat(topics[1].topic).isEqualTo("Geography")
    }

    @Test
    fun `getAllTopics returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllTopics()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllTopics returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllTopics()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllTopics returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllTopics()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val topics = (result as Result.Success).data
        assertThat(topics).isEmpty()
    }

    @Test
    fun `getTopicById returns success with topic when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "topicId": 1,
                "topic": "Physics"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getTopicById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val topic = (result as Result.Success).data
        assertThat(topic.topicId).isEqualTo(1)
        assertThat(topic.topic).isEqualTo("Physics")
    }

    @Test
    fun `getTopicById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Topic not found")
        )

        val result = repository.getTopicById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getTopicById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "topicId": 42,
                "topic": "Test Topic"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getTopicById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/topics/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "topicId": 123,
                "topic": "Advanced Chemistry"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getTopicById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val topic = (result as Result.Success).data
        assertThat(topic.topicId).isEqualTo(123)
        assertThat(topic.topic).isEqualTo("Advanced Chemistry")
    }
}
