package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.AuthApiService
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
 * Integration test for AuthRepositoryImpl using MockWebServer.
 * Tests Repository + ApiService + Retrofit + DTO mapping together.
 */
class AuthRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: AuthApiService
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(AuthApiService::class.java)
        repository = AuthRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ========== Login Tests ==========

    @Test
    fun `login returns success with JWT token when API responds 200`() = runTest {
        // Given: Mock successful login response
        val jsonResponse = """
            {
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0.abc123",
                "type": "Bearer",
                "email": "test@test.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls login
        val result = repository.login("test@test.com", "password123")

        // Then: Success with JWT token
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val jwtResponse = (result as Result.Success).data
        assertThat(jwtResponse.token).startsWith("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        assertThat(jwtResponse.type).isEqualTo("Bearer")
        assertThat(jwtResponse.email).isEqualTo("test@test.com")
    }

    @Test
    fun `login sends correct request body to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "token": "test-token",
                "type": "Bearer",
                "email": "user@test.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls login with specific credentials
        repository.login("user@test.com", "mypassword")

        // Then: Verify correct endpoint and request body
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/api/auth/login")
        assertThat(request.method).isEqualTo("POST")
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"email\":\"user@test.com\"")
        assertThat(requestBody).contains("\"password\":\"mypassword\"")
    }

    @Test
    fun `login returns error when API responds with 401 Unauthorized`() = runTest {
        // Given: Mock unauthorized response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("Invalid credentials")
        )

        // When: Repository calls login
        val result = repository.login("wrong@test.com", "wrongpass")

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `login returns error when API responds with 500`() = runTest {
        // Given: Mock server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls login
        val result = repository.login("test@test.com", "password")

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `login returns error when network fails`() = runTest {
        // Given: Server shutdown (simulates network error)
        mockWebServer.shutdown()

        // When: Repository calls login
        val result = repository.login("test@test.com", "password")

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // ========== Register Tests ==========

    @Test
    fun `register returns success with JWT token when API responds 200`() = runTest {
        // Given: Mock successful registration response
        val jsonResponse = """
            {
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newuser",
                "type": "Bearer",
                "email": "newuser@test.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls register
        val result = repository.register(
            email = "newuser@test.com",
            password = "password123",
            firstname = "John",
            surname = "Doe",
            birthdate = "01.01.2000",
            nickname = "johnd"
        )

        // Then: Success with JWT token
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val jwtResponse = (result as Result.Success).data
        assertThat(jwtResponse.token).isNotEmpty()
        assertThat(jwtResponse.type).isEqualTo("Bearer")
        assertThat(jwtResponse.email).isEqualTo("newuser@test.com")
    }

    @Test
    fun `register sends correct request body to API`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "token": "test-token",
                "type": "Bearer",
                "email": "test@test.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls register
        repository.register(
            email = "test@test.com",
            password = "pass123",
            firstname = "Max",
            surname = "Mustermann",
            birthdate = "15.06.2000",
            nickname = "maxm"
        )

        // Then: Verify correct endpoint and request body
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/api/auth/register")
        assertThat(request.method).isEqualTo("POST")
        val requestBody = request.body.readUtf8()
        assertThat(requestBody).contains("\"email\":\"test@test.com\"")
        assertThat(requestBody).contains("\"password\":\"pass123\"")
        assertThat(requestBody).contains("\"firstname\":\"Max\"")
        assertThat(requestBody).contains("\"surname\":\"Mustermann\"")
        assertThat(requestBody).contains("\"birthdate\":\"15.06.2000\"")
        assertThat(requestBody).contains("\"nickname\":\"maxm\"")
    }

    @Test
    fun `register with null nickname sends correct request`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "token": "test-token",
                "type": "Bearer",
                "email": "test@test.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls register without nickname
        repository.register(
            email = "test@test.com",
            password = "pass123",
            firstname = "Max",
            surname = "Mustermann",
            birthdate = "15.06.2000",
            nickname = null
        )

        // Then: Request should handle null nickname
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/api/auth/register")
        assertThat(request.method).isEqualTo("POST")
    }

    @Test
    fun `register returns error when API responds with 400 Bad Request`() = runTest {
        // Given: Mock bad request response (e.g., email already exists)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("Email already exists")
        )

        // When: Repository calls register
        val result = repository.register(
            email = "existing@test.com",
            password = "password",
            firstname = "John",
            surname = "Doe",
            birthdate = "01.01.2000",
            nickname = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `register returns error when API responds with 500`() = runTest {
        // Given: Mock server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls register
        val result = repository.register(
            email = "test@test.com",
            password = "password",
            firstname = "John",
            surname = "Doe",
            birthdate = "01.01.2000",
            nickname = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `register returns error when network fails`() = runTest {
        // Given: Server shutdown (simulates network error)
        mockWebServer.shutdown()

        // When: Repository calls register
        val result = repository.register(
            email = "test@test.com",
            password = "password",
            firstname = "John",
            surname = "Doe",
            birthdate = "01.01.2000",
            nickname = null
        )

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `DTO mapping converts all JWT fields correctly`() = runTest {
        // Given: Mock response with specific token format
        val jsonResponse = """
            {
                "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNjQwMDAwMDAwLCJleHAiOjE2NDAwMDM2MDB9.signature",
                "type": "Bearer",
                "email": "test@example.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls login
        val result = repository.login("test@example.com", "password")

        // Then: All JWT fields correctly mapped from DTO
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val jwtResponse = (result as Result.Success).data
        assertThat(jwtResponse.token).isEqualTo("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNjQwMDAwMDAwLCJleHAiOjE2NDAwMDM2MDB9.signature")
        assertThat(jwtResponse.type).isEqualTo("Bearer")
        assertThat(jwtResponse.email).isEqualTo("test@example.com")
    }
}
