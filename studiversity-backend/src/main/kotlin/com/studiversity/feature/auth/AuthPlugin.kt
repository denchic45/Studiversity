package com.studiversity.feature.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.studiversity.config
import com.studiversity.util.isEmail
import com.studiversity.util.respondWithError
import com.studiversity.validation.ValidationResultBuilder
import com.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.util.ErrorInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*

fun Application.configureAuth() {

    val jwtJWTSecret = config.jwt.secret
    val jwtJWTAudience = config.jwt.audience

    authentication {
        jwt("auth-jwt") {
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
            recoverRoute()
            resetRoute()
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