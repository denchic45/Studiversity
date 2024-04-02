package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.feature.user.account.routing.accountRoutes
import io.ktor.server.application.*

fun Application.configureUsers() {
    userRoutes()
    accountRoutes()
}

object UserErrors {
    const val USER_NOT_FOUND = "USER_NOT_FOUND"
}