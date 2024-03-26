package com.denchic45.studiversity.feature.user.account.routing

import com.denchic45.studiversity.feature.user.account.usecase.ConfirmAccountActionUseCase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.accountRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/account") {
                personalRoute()
                securityRoute()
            }
        }

        val confirmAccountAction: ConfirmAccountActionUseCase by inject()

        get("/account-confirm") {
            val token = call.request.queryParameters.getOrFail("token")

        }
    }
}