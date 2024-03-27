package com.denchic45.studiversity.util

import com.denchic45.studiversity.api.GeneralErrors
import io.ktor.server.plugins.*
import java.util.*

fun String.tryToUUID(): UUID = try {
    toUUID()
} catch (t: Throwable) {
    throw BadRequestException(GeneralErrors.INVALID_UUID)
}