package com.denchic45.studiversity.feature.user.account.routing

import com.denchic45.studiversity.feature.user.account.usecase.ConfirmEmailUseCase
import com.denchic45.studiversity.ktor.getUuidOrFail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.accountRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/account") {
                personalRoute()
                securityRoute()
            }
        }

        val confirmEmail: ConfirmEmailUseCase by inject()

        get("/email-confirmation") {
            val token = call.request.queryParameters.getUuidOrFail("token")
            if (confirmEmail(token))
                call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.Gone)
        }
    }
}