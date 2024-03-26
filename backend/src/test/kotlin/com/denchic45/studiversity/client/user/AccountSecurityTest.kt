package com.denchic45.studiversity.client.user

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertedResultIsError
import com.denchic45.studiversity.util.assertedResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.studiversity.util.unwrapAssertedError
import com.denchic45.stuiversity.api.account.AccountApi
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.CompositeError
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.get
import org.koin.test.inject
import kotlin.test.assertEquals


class AccountSecurityTest : KtorClientTest() {

    private val userApi: UserApi by inject { parametersOf(client) }
    private val accountApi: AccountApi
        get() = get { parametersOf(userClient) }
    private val userApiOfUser: UserApi
        get() = get { parametersOf(userClient) }

    private val email = "petya@gmail.com"
    private val password = "h9gf90G90v854"

    private lateinit var user: UserResponse
    private lateinit var userClient: HttpClient

    @BeforeEach
    fun init(): Unit = runBlocking {
        authApiOfGuest.signup(SignupRequest("Petya", "Ivanov", null, Gender.MALE,email, password))
            .assertedResultIsOk()
        userClient = createAuthenticatedClient(email, password)
        user = userApiOfUser.getMe().unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
    }

    @Test
    fun testUpdateEmail(): Unit = runBlocking {
        // Wrong email
        accountApi.updateEmail("another.mail.ru").unwrapAssertedError().apply {
            assertEquals(listOf(AuthErrors.INVALID_EMAIL), (error as? CompositeError)?.reasons)
        }

        accountApi.updateEmail("another@mail.ru").assertedResultIsOk()
    }

    @Test
    fun testUpdatePassword(): Unit = runBlocking {
        val newPassword = "iuhBf4309g90g"

        // Wrong old password
        accountApi.updatePassword(UpdatePasswordRequest("23ADSong43g43", newPassword)).assertedResultIsError()
        // Bad password
        accountApi.updatePassword(UpdatePasswordRequest("123", newPassword)).assertedResultIsError()

        accountApi.updatePassword(UpdatePasswordRequest(password, newPassword)).assertedResultIsOk()
        authApiOfGuest.signInByEmailPassword(SignInByEmailPasswordRequest(email, newPassword)).assertedResultIsOk()
    }
}