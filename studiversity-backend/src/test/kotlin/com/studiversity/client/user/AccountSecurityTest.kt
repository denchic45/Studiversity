package com.studiversity.client.user

import com.studiversity.KtorClientTest
import com.studiversity.util.assertedResultIsError
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.studiversity.util.unwrapAssertedError
import com.denchic45.stuiversity.api.account.AccountApi
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.User
import com.denchic45.stuiversity.util.ErrorValidation
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

    private lateinit var user: User
    private lateinit var userClient: HttpClient

    @BeforeEach
    fun init(): Unit = runBlocking {
        authApiOfGuest.signup(SignupRequest("Petya", "Ivanov", null, email, password))
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
        accountApi.updateEmail(UpdateEmailRequest("another.mail.ru")).unwrapAssertedError().apply {
            assertEquals(listOf(AuthErrors.INVALID_EMAIL), (error as? ErrorValidation)?.reasons)
        }

        accountApi.updateEmail(UpdateEmailRequest("another@mail.ru")).assertedResultIsOk()
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