package com.studiversity.util

import io.ktor.server.plugins.*
import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)

fun String.tryToUUID():UUID = try {
    toUUID()
} catch (t:Throwable) {
    throw BadRequestException("INVALID_UUID")
}