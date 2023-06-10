package com.studiversity.client.user

import com.github.michaelbull.result.unwrap
import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class CreateUserTest : KtorClientTest() {

    private val userApi: UserApi by inject { parametersOf(client) }
    private val email = "denchic150@gmail.com"
    private val expectedFirstName = "Yaroslav"
    private val expectedSurname = "Sokolov"
    private val userApiOfModerator: UserApi by inject { parametersOf(client) }

    @Test
    fun test(): Unit = runBlocking {
        val user: UserResponse = userApi.create(CreateUserRequest(expectedFirstName, expectedSurname, null, email))
            .unwrapAsserted()

        userApiOfModerator.getById(user.id).also(::assertResultIsOk).unwrap().apply {
            assertEquals(expectedFirstName, firstName)
            assertEquals(expectedSurname, surname)
        }

        userApiOfModerator.delete(user.id).also(::assertResultIsOk)
    }
}