package com.studiversity.client.user

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchUsersTest : KtorClientTest() {
    private val userApi: UserApi by inject { parametersOf(client) }
    private val email = "denchic150@gmail.com"
    private val expectedFirstName = "Yaroslav"
    private val expectedSurname = "Sokolov"
    private val expectedPatronymic = "Dmitrievich"

    private lateinit var user: UserResponse

    @BeforeEach
    fun init(): Unit = runBlocking {
        user = userApi.create(CreateUserRequest(expectedFirstName, expectedSurname, expectedPatronymic, email))
            .unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
    }

    @Test
    fun test(): Unit = runBlocking {
        userApi.search("Yaro").also(::assertResultIsOk).unwrap().single().apply {
            assertEquals(expectedFirstName, firstName)
            assertEquals(expectedSurname, surname)
        }

        userApi.search("sokol").also(::assertResultIsOk).unwrap().single().apply {
            assertEquals(expectedFirstName, firstName)
            assertEquals(expectedSurname, surname)
        }

        userApi.search("dmit").also(::assertResultIsOk).unwrap().single().apply {
            assertEquals(expectedFirstName, firstName)
            assertEquals(expectedSurname, surname)
        }

        userApi.search("okolov").also(::assertResultIsOk).unwrap().apply {
            assertEquals(emptyList(), this)
        }
    }
}