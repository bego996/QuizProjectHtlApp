package com.app.quizapp.data.repository

import com.app.quizapp.data.remote.UserRoleApiService
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
 * Integration test for UserRoleRepositoryImpl using MockWebServer.
 */
class UserRoleRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: UserRoleApiService
    private lateinit var repository: UserRoleRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        apiService = retrofit.create(UserRoleApiService::class.java)
        repository = UserRoleRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getAllUserRoles returns success with userRoles list when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {
                    "userRoleId": 1,
                    "userRole": "Student"
                },
                {
                    "userRoleId": 2,
                    "userRole": "Teacher"
                },
                {
                    "userRoleId": 3,
                    "userRole": "Admin"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRoles = (result as Result.Success).data
        assertThat(userRoles).hasSize(3)
        assertThat(userRoles[0].userRole).isEqualTo("Student")
        assertThat(userRoles[1].userRole).isEqualTo("Teacher")
        assertThat(userRoles[2].userRole).isEqualTo("Admin")
    }

    @Test
    fun `getAllUserRoles returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getAllUserRoles returns error when network fails`() = runTest {
        mockWebServer.shutdown()

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getAllUserRoles returns empty list when API returns empty array`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRoles = (result as Result.Success).data
        assertThat(userRoles).isEmpty()
    }

    @Test
    fun `getUserRoleById returns success with userRole when API responds 200`() = runTest {
        val jsonResponse = """
            {
                "userRoleId": 1,
                "userRole": "Moderator"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getUserRoleById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRole = (result as Result.Success).data
        assertThat(userRole.userRoleId).isEqualTo(1)
        assertThat(userRole.userRole).isEqualTo("Moderator")
    }

    @Test
    fun `getUserRoleById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("UserRole not found")
        )

        val result = repository.getUserRoleById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = (result as Result.Error).message
        assertThat(error).isNotEmpty()
    }

    @Test
    fun `getUserRoleById verifies correct endpoint is called`() = runTest {
        val jsonResponse = """
            {
                "userRoleId": 42,
                "userRole": "Test Role"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        repository.getUserRoleById(42)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/userRoles/42")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `DTO mapping converts all fields correctly`() = runTest {
        val jsonResponse = """
            {
                "userRoleId": 123,
                "userRole": "Super Admin"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = repository.getUserRoleById(123)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRole = (result as Result.Success).data
        assertThat(userRole.userRoleId).isEqualTo(123)
        assertThat(userRole.userRole).isEqualTo("Super Admin")
    }
}
