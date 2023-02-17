package com.studiversity.feature.user.account.routing

import com.studiversity.feature.auth.addPasswordConditions
import com.studiversity.feature.user.account.usecase.UpdateEmailUseCase
import com.studiversity.feature.user.account.usecase.UpdatePasswordUseCase
import com.studiversity.ktor.currentUserId
import com.studiversity.util.isEmail
import com.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.auth.AuthErrors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.securityRoute() {
    val updatePassword: UpdatePasswordUseCase by inject()
    val updateEmail: UpdateEmailUseCase by inject()
    install(RequestValidation) {
        validate<UpdatePasswordRequest> { request ->
            buildValidationResult {
                addPasswordConditions(request.oldPassword)
            }
        }
        validate<UpdateEmailRequest> { request ->
            buildValidationResult {
                condition(request.email.isEmail(), AuthErrors.INVALID_EMAIL)
            }
        }
    }
    post("/password") {
        updatePassword(call.currentUserId(), call.receive())
        call.respond(HttpStatusCode.OK)
    }
    post("/email") {
        updateEmail(call.currentUserId(), call.receive())
        call.respond(HttpStatusCode.OK)
    }
}