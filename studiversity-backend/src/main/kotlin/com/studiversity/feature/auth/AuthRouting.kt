package com.studiversity.feature.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.studiversity.di.JwtEnv
import com.studiversity.di.OrganizationEnv
import com.studiversity.feature.auth.usecase.*
import com.studiversity.ktor.ForbiddenException
import com.stuiversity.api.auth.AuthErrors
import com.stuiversity.api.auth.model.SignupRequest
import com.stuiversity.api.auth.model.TokenResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import java.util.*

fun Route.signupRoute() {
    val signup: SignUpUseCase by inject()
    val selfRegister: Boolean by inject(named(OrganizationEnv.ORG_SELF_REGISTER))

    post("/signup") {
        if (!selfRegister) throw ForbiddenException()
        val signupRequest = call.receive<SignupRequest>()
        signup(signupRequest)
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.tokenRoute() {
    val signInByEmailAndPasswordUseCase: SignInByEmailAndPasswordUseCase by inject()
    val refreshToken: RefreshTokenUseCase by inject()
    val jwtSecret: String by inject(named(JwtEnv.JWT_SECRET))
    val audience: String by inject(named(JwtEnv.JWT_AUDIENCE))

    post("/token") {
        val userIdWithToken = when (call.request.queryParameters["grant_type"]) {
            "password" -> signInByEmailAndPasswordUseCase(call.receive())
            "refresh_token" -> refreshToken(call.receive())
            else -> throw BadRequestException(AuthErrors.INVALID_GRANT)
        }

        val token = JWT.create()
            .withAudience(audience)
            .withSubject(userIdWithToken.first.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(HttpStatusCode.OK, TokenResponse(token, userIdWithToken.second))
    }
}

fun Route.recoverRoute() {
    val recoverPassword: RecoverPasswordUseCase by inject()
    post("/recover") {
        recoverPassword(call.receive(), url {
            host = call.request.host()
            port = call.request.port()
            path("auth", "reset")
        }
        )
        call.respond(HttpStatusCode.Accepted)
    }
}

fun Route.resetRoute() {
    val checkMagicLinkToken: CheckMagicLinkTokenUseCase by inject()
    post("/recover") {
        checkMagicLinkToken(call.request.queryParameters.getOrFail("token"))
        call.respondText(
            """
                
        """.trimIndent(),
            ContentType.Text.Html
        )
    }
}