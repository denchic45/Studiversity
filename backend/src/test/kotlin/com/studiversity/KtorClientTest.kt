package com.studiversity

import com.denchic45.studiversity.client.di.apiModule
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.auth.AuthApi
import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.github.michaelbull.result.onSuccess
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.inject

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KtorClientTest : KoinTest {

    private lateinit var testApp: TestApplication
    lateinit var client: HttpClient
    val authApiOfGuest: AuthApi by inject { parametersOf(createGuestClient()) }

    @BeforeAll
    fun beforeAll(): Unit = runBlocking {
        testApp = TestApplication {
            application {
                buildApplication()
            }
        }
        testApp.start()
        client = createAuthenticatedClient("denchic860@gmail.com", "JFonij5430")
        setup()
    }

    @AfterAll
    fun afterAll() = runBlocking {
        cleanup()
        testApp.stop()
    }

    open fun setup() {}

    open fun Application.buildApplication() {
        module()
        loadKoinModules(apiModule)
    }

    open fun cleanup() {}

    fun createGuestClient() = testApp.createClient {
        installContentNegotiation()
    }

    fun createAuthenticatedClient(email: String, password: String) = testApp.createClient {
        installContentNegotiation()
        install(Auth) {
            var bearerTokens = runBlocking {
                authApiOfGuest.signInByEmailPassword(SignInByEmailPasswordRequest(email, password)).unwrapAsserted()
                    .run {
                        BearerTokens(this.token, this.refreshToken)
                    }
            }
            bearer {
                loadTokens {
                    bearerTokens
                }
                refreshTokens {
                    val result = authApiOfGuest.refreshToken(RefreshTokenRequest(oldTokens!!.refreshToken))
                    result.onSuccess {
                        bearerTokens = BearerTokens(it.token, it.refreshToken)
                    }
                    bearerTokens
                }
            }
        }
    }
}

private fun HttpClientConfig<out HttpClientEngineConfig>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}