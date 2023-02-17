package com.denchic45.stuiversity.util

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
class ErrorResponse(val code: Int, val error: ErrorContent)

fun List<String>.toErrors(): ErrorValidation {
    return ErrorValidation(this)
}

@Serializable(ErrorResponseSerializer::class)
sealed interface ErrorContent

@Serializable
data class ErrorInfo(val reason: String) : ErrorContent

@Serializable
data class ErrorValidation(val reasons: List<String>) : ErrorContent

class ErrorResponseSerializer : JsonContentPolymorphicSerializer<ErrorContent>(ErrorContent::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ErrorContent> {
        val jsonObject = element.jsonObject
        return when {
            jsonObject.contains("reason") -> ErrorInfo.serializer()
            jsonObject.contains("reasons") -> ErrorValidation.serializer()
            else -> throw IllegalStateException()
        }
    }
}