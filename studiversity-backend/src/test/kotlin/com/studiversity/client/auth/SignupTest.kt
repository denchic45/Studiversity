package com.studiversity.client.auth

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsError
import com.studiversity.util.assertResultIsOk
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.UserApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SignupTest : KtorClientTest() {

    private val email = "denchic150@gmail.com"
    private val password = "KLNf94fghn4gg"
    private val expectedFirstName = "Yaroslav"
    private val expectedSurname = "Sokolov"

    private val userClient by lazy { createAuthenticatedClient(email, password) }

    private val userApiOfUser: UserApi by inject { parametersOf(userClient) }
    private val userApiOfModerator: UserApi by inject { parametersOf(client) }

    @Test
    fun testSignup(): Unit = runBlocking {
        val signupRequest = SignupRequest(expectedFirstName, expectedSurname, null, email, password)
        authApiOfGuest.signup(signupRequest).also(::assertResultIsOk).unwrap()

        // User already registered
        authApiOfGuest.signup(signupRequest).also(::assertResultIsError)

        val user = userApiOfUser.getMe().also(::assertResultIsOk).unwrap().apply {
            assertEquals(expectedFirstName, firstName)
            assertEquals(expectedSurname, surname)
        }
        userApiOfModerator.delete(user.id).also(::assertResultIsOk)
    }
}