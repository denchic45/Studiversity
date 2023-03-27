package com.studiversity.util

import com.denchic45.stuiversity.util.toUUID
import io.ktor.server.plugins.*
import java.util.*

fun String.tryToUUID(): UUID = try {
    toUUID()
} catch (t:Throwable) {
    throw BadRequestException("INVALID_UUID")
}