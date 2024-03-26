package com.denchic45.studiversity.feature.user.account.routing

import com.denchic45.studiversity.feature.user.account.usecase.UpdateEmailUseCase
import com.denchic45.studiversity.feature.user.account.usecase.UpdatePasswordUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.util.isEmail
import com.denchic45.stuiversity.api.auth.AuthErrors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.securityRoute() {
    val updatePassword: UpdatePasswordUseCase by inject()
    val updateEmail: UpdateEmailUseCase by inject()

    post("/password") {
        updatePassword(call.currentUserId(), call.receive())
        call.respond(HttpStatusCode.OK)
    }
    post("/email") {
        val email = call.receiveText()
        if (!email.isEmail()) throw BadRequestException(AuthErrors.INVALID_EMAIL)
        updateEmail(
            userId = call.currentUserId(),
            email = email,
            url = url {
                host = call.request.host()
                port = call.request.port()
                path("email-confirmation")
            })
        call.respond(HttpStatusCode.OK)
    }
}