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
    fun `getAllUserRoles returns success when API responds 200`() = runTest {
        val jsonResponse = """
            [
                {"userRoleId": 1, "userRole": "ROLE_USER"},
                {"userRoleId": 2, "userRole": "ROLE_ADMIN"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRoles = (result as Result.Success).data
        assertThat(userRoles).hasSize(2)
        assertThat(userRoles[0].userRole).isEqualTo("ROLE_USER")
        assertThat(userRoles[1].userRole).isEqualTo("ROLE_ADMIN")
    }

    @Test
    fun `getAllUserRoles returns error when API responds with 500`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = repository.getAllUserRoles()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `getUserRoleById returns success when API responds 200`() = runTest {
        val jsonResponse = """{"userRoleId": 1, "userRole": "ROLE_USER"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.getUserRoleById(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val userRole = (result as Result.Success).data
        assertThat(userRole.userRoleId).isEqualTo(1)
        assertThat(userRole.userRole).isEqualTo("ROLE_USER")
    }

    @Test
    fun `getUserRoleById returns error when API responds with 404`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("UserRole not found"))

        val result = repository.getUserRoleById(999)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `deleteUserRole returns success when API responds 200`() = runTest {
        val jsonResponse = """{"userRoleId": 5, "userRole": "ROLE_MODERATOR"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val result = repository.deleteUserRole(5)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val deletedUserRole = (result as Result.Success).data
        assertThat(deletedUserRole.userRoleId).isEqualTo(5)
    }

    @Test
    fun `deleteUserRole verifies correct endpoint is called`() = runTest {
        val jsonResponse = """{"userRoleId": 10, "userRole": "ROLE_TEST"}"""

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        repository.deleteUserRole(10)

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/10")
        assertThat(request.method).isEqualTo("DELETE")
    }
}
