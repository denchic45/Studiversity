package com.studiversity.validation

import io.ktor.server.plugins.*

fun <T> T.require(predicate: (T) -> Boolean, lazyMessage: () -> String): T {
    return if (predicate(this)) this
    else throw BadRequestException(lazyMessage())
}