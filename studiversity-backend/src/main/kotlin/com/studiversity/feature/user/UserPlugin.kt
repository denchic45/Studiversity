package com.studiversity.feature.user

import com.studiversity.feature.user.account.routing.accountRoutes
import io.ktor.server.application.*

fun Application.configureUser() {
    userRoutes()
    accountRoutes()
}