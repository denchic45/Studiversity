package com.denchic45.studiversity.feature.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.denchic45.studiversity.config.config
import com.denchic45.studiversity.feature.auth.usecase.*
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.stuiversity.api.auth.AuthErrors
import com.denchic45.stuiversity.api.auth.model.SignInResponse
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.util.toDate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

fun Route.signupRoute() {
    val signup: SignUpUseCase by inject()

    post("/signup") {
        if (!config.selfRegister) throw ForbiddenException()
        val signupRequest = call.receive<SignupRequest>()
        signup(signupRequest)
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.tokenRoute() {
    val signInByEmailAndPasswordUseCase: SignInByEmailAndPasswordUseCase by inject()
    val refreshToken: RefreshTokenUseCase by inject()

    post("/token") {
        val userIdWithToken = when (call.request.queryParameters["grant_type"]) {
            "password" -> signInByEmailAndPasswordUseCase(call.receive())
            "refresh_token" -> refreshToken(call.receive())
            else -> throw BadRequestException(AuthErrors.INVALID_GRANT)
        }

        val token = JWT.create()
            .withAudience(config.jwtAudience)
            .withSubject(userIdWithToken.first.toString())
            .withExpiresAt(LocalDateTime.now().plusHours(1).toDate())
            .sign(Algorithm.HMAC256(config.jwtSecret))

        call.respond(HttpStatusCode.OK, SignInResponse(token, userIdWithToken.second, config.organizationId))
    }
}

fun Route.recoverPasswordRoute() {
    val recoverPassword: RecoverPasswordUseCase by inject()
    val checkConfirmCode: CheckConfirmCodeUseCase by inject()
    val updateRecoveredPassword: UpdateRecoveredPasswordUseCase by inject()

    post("/recover-password") {
        recoverPassword(call.receiveText())
        call.respond(HttpStatusCode.Accepted)
    }
    post("/password") {
        val code = call.request.queryParameters.getOrFail("code")
        if (!checkConfirmCode(code)) {
            call.respond(HttpStatusCode.Gone)
            return@post
        }

        updateRecoveredPassword(code, call.receiveText())
        call.respond(HttpStatusCode.OK)
    }
}