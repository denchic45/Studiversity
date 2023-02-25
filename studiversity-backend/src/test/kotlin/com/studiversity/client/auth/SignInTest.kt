package com.studiversity.client.auth

import com.studiversity.KtorClientTest
import com.studiversity.database.table.RefreshTokens
import com.studiversity.util.assertedResultIsError
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject

class SignInTest : KtorClientTest() {
    private val userApi: UserApi by inject { parametersOf(client) }
    private val userApiOfUser: UserApi by inject { parametersOf(userClient) }

    private val email = "petya@gmail.com"
    private val password = "h9gf90G90v854"

    private lateinit var user: UserResponse
    private lateinit var userClient: HttpClient

    @BeforeEach
    fun init(): Unit = runBlocking {
        authApiOfGuest.signup(SignupRequest("Nikita", "Volkov", null, email, password))
            .assertedResultIsOk()
        userClient = createAuthenticatedClient(email, password)
        user = userApiOfUser.getMe().unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
    }

    @Test
    fun testReAuthOnExpiredRefreshToken(): Unit = runBlocking {
        userApiOfUser.getMe().assertedResultIsOk()

        transaction { RefreshTokens.deleteWhere { userId eq user.id } }

        delay(5000)

        userApiOfUser.getMe().assertedResultIsError()
    }
}