package com.studiversity.feature.user.account.routing

import com.studiversity.feature.user.account.usecase.UpdateAccountPersonalUseCase
import com.studiversity.ktor.currentUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.personalRoute() {
    val updateAccountPersonal: UpdateAccountPersonalUseCase by inject()
    post("/personal") {
        updateAccountPersonal(call.currentUserId(), call.receive())
        call.respond(HttpStatusCode.OK)
    }
}