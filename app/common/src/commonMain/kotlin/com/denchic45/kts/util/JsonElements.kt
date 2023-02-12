package com.denchic45.kts.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType

fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Array<*> -> JsonArray(map { it.toJsonElement() })
    is List<*> -> JsonArray(map { it.toJsonElement() })
    is Map<*, *> -> JsonObject(map { it.key.toString() to it.value.toJsonElement() }.toMap())
    else -> Json.encodeToJsonElement(serializer(this::class.createType()), this)
}

fun Any?.toJsonString(): String = Json.encodeToString(this.toJsonElement())