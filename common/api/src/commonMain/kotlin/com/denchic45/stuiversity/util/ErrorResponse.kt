package com.denchic45.stuiversity.util

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
class ErrorResponse(val code: Int, val error: ErrorContent)

fun List<String>.toErrors(): CompositeError {
    return CompositeError(this)
}

@Serializable(ErrorResponseSerializer::class)
sealed interface ErrorContent {
    val reason: String
}

@Serializable
data class ErrorInfo(override val reason: String) : ErrorContent

@Serializable
data class CompositeError(
    val reasons: List<String>,
    override val reason: String = ValidationError.INVALID_REQUEST,
) : ErrorContent

class ErrorResponseSerializer :
    JsonContentPolymorphicSerializer<ErrorContent>(ErrorContent::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ErrorContent> {
        val jsonObject = element.jsonObject
        return when {
            jsonObject.contains("reasons") -> CompositeError.serializer()
            else -> ErrorInfo.serializer()
        }
    }
}

object ValidationError {
    const val INVALID_REQUEST = "INVALID_REQUEST"
}