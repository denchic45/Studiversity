package com.denchic45.studiversity.util

import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    return try {
        val text = bodyAsText()
        Json.decodeFromString<T>(text)
    } catch (e: Exception) {
        null
    }
}