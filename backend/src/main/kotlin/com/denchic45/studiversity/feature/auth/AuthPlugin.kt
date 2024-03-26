package com.denchic45.studiversity.feature.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.auth.usecase.CheckConfirmCodeUseCase
import com.denchic45.studiversity.util.isEmail
import com.denchic45.studiversity.util.respondWithError
import com.denchic45.studiversity.validation.ValidationResultBuilder
import com.denchic45.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.util.ErrorInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.configureAuth() {

    val jwtJWTSecret = config.jwtSecret
    val jwtJWTAudience = config.jwtAudience
    val jwtRealm = config.jwtRealm

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtJWTSecret))
                    .withAudience(jwtJWTAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtJWTAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { defaultScheme, realm ->
                call.respondWithError(HttpStatusCode.Unauthorized, ErrorInfo("Token is not valid or has expired"))
            }
        }
    }

    routing {
        route("/auth") {
            val checkConfirmationCode: CheckConfirmCodeUseCase by inject()

            install(RequestValidation) {
                validate<SignupRequest> { login ->
                    val password = login.password
                    buildValidationResult {
                        addPasswordConditions(password)
                        condition(login.email.isEmail(), AuthErrors.INVALID_EMAIL)
                    }
                }
            }
            signupRoute()
            tokenRoute()
            recoverPasswordRoute()

            get("/confirmation-code") {
                val code = call.request.queryParameters.getOrFail("code")
                if (checkConfirmationCode(code)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Gone)
            }
        }
    }
}

fun ValidationResultBuilder.addPasswordConditions(password: String) {
    condition(password.length >= 6, AuthErrors.PASSWORD_MUST_CONTAIN_AT_LEAST_6_CHARACTERS)
    condition(
        password.contains("(?=.*[a-z])(?=.*[A-Z])".toRegex()),
        AuthErrors.PASSWORD_MUST_CONTAIN_UPPER_AND_LOWER_CASE_CHARACTERS
    )
    condition(password.contains("[0-9]".toRegex()), AuthErrors.PASSWORD_MUST_CONTAIN_DIGITS)
}