package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserApiService
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
 * Integration test for UserRepositoryImpl using MockWebServer.
 * Tests Repository + ApiService + Retrofit + DTO mapping together.
 */
class UserRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: UserApiService
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(UserApiService::class.java)
        repository = UserRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllUsers returns success with users list when API responds 200`() = runTest {
        // Given: Mock successful API response
        val jsonResponse = """
            [
                {
                    "userId": 1,
                    "surname": "Mustermann",
                    "firstname": "Max",
                    "birthdate": "2000-01-01",
                    "nickname": "maxm",
                    "email": "max@test.com",
                    "password": "test123",
                    "userRole": {
                        "userRoleId": 1,
                        "userRole": "Student"
                    }
                },
                {
                    "userId": 2,
                    "surname": "Doe",
                    "firstname": "Jane",
                    "birthdate": "1999-05-15",
                    "nickname": "janed",
                    "email": "jane@test.com",
                    "password": "test456",
                    "userRole": {
                        "userRoleId": 1,
                        "userRole": "Student"
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getAllUsers
        val result = repository.getAllUsers()

        // Then: Success with parsed domain models
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val users = (result as Result.Success).data
        assertThat(users).hasSize(2)
        assertThat(users[0].firstname).isEqualTo("Max")
        assertThat(users[0].surname).isEqualTo("Mustermann")
        assertThat(users[0].email).isEqualTo("max@test.com")
        assertThat(users[0].userRole.userRole).isEqualTo("Student")
        assertThat(users[1].firstname).isEqualTo("Jane")
    }

    @Test
    fun `getAllUsers returns error when API responds with 500`() = runTest {
        // Given: Mock error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When: Repository calls getAllUsers
        val result = repository.getAllUsers()

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllUsers returns error when network fails`() = runTest {
        // Given: Server shutdown (simulates network error)
        mockWebServer.shutdown()

        // When: Repository calls getAllUsers
        val result = repository.getAllUsers()

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllUsers returns empty list when API returns empty array`() = runTest {
        // Given: Mock empty response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When: Repository calls getAllUsers
        val result = repository.getAllUsers()

        // Then: Success with empty list
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val users = (result as Result.Success).data
        assertThat(users).isEmpty()
    }

    @Test
    fun `getUserById returns success with user when API responds 200`() = runTest {
        // Given: Mock successful API response
        val jsonResponse = """
            {
                "userId": 1,
                "surname": "Mustermann",
                "firstname": "Max",
                "birthdate": "2000-01-01",
                "nickname": "maxm",
                "email": "max@test.com",
                "password": "test123",
                "userRole": {
                    "userRoleId": 1,
                    "userRole": "Student"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getUserById
        val result = repository.getUserById(1)

        // Then: Success with parsed user
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val user = (result as Result.Success).data
        assertThat(user.userId).isEqualTo(1)
        assertThat(user.firstname).isEqualTo("Max")
        assertThat(user.surname).isEqualTo("Mustermann")
        assertThat(user.email).isEqualTo("max@test.com")
        assertThat(user.userRole.userRole).isEqualTo("Student")
    }

    @Test
    fun `getUserById returns error when API responds with 404`() = runTest {
        // Given: Mock 404 response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("User not found")
        )

        // When: Repository calls getUserById
        val result = repository.getUserById(999)

        // Then: Error result
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getUserById verifies correct endpoint is called`() = runTest {
        // Given: Mock successful response
        val jsonResponse = """
            {
                "userId": 42,
                "surname": "Test",
                "firstname": "User",
                "birthdate": "2000-01-01",
                "nickname": "testuser",
                "email": "test@test.com",
                "password": "test",
                "userRole": {
                    "userRoleId": 1,
                    "userRole": "Student"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getUserById with specific id
        repository.getUserById(42)

        // Then: Verify correct endpoint was called
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/users/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        // Given: Mock response with all fields
        val jsonResponse = """
            {
                "userId": 123,
                "surname": "Schmidt",
                "firstname": "Anna",
                "birthdate": "1998-12-25",
                "nickname": "annas",
                "email": "anna@example.com",
                "password": "secret",
                "userRole": {
                    "userRoleId": 2,
                    "userRole": "Teacher"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // When: Repository calls getUserById
        val result = repository.getUserById(123)

        // Then: All fields correctly mapped from DTO to Domain
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val user = (result as Result.Success).data
        assertThat(user.userId).isEqualTo(123)
        assertThat(user.surname).isEqualTo("Schmidt")
        assertThat(user.firstname).isEqualTo("Anna")
        assertThat(user.birthdate).isEqualTo("1998-12-25")
        assertThat(user.nickname).isEqualTo("annas")
        assertThat(user.email).isEqualTo("anna@example.com")
        assertThat(user.password).isEqualTo("secret")
        assertThat(user.userRole.userRoleId).isEqualTo(2)
        assertThat(user.userRole.userRole).isEqualTo("Teacher")
    }
}
