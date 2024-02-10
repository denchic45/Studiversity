package com.denchic45.studiversity.feature.auth

import com.denchic45.studiversity.feature.auth.usecase.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private val useCaseModule = module {
    single { SignUpUseCase(get(), get()) }
    single { SignUpUserManuallyUseCase(get(), get(), get(), get()) }
    single { SignInByEmailAndPasswordUseCase(get(), get()) }
    single { RefreshTokenUseCase(get(), get()) }
    single { RecoverPasswordUseCase(get(), get(), get()) }
    single { CheckMagicLinkTokenUseCase(get(), get()) }
}

val authModule = module {
    includes(useCaseModule)
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                })
            }
        }
    }
}