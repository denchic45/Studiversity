package com.denchic45.studiversity.firebasemultiplatform.api

enum class ServerValue { REQUEST_TIME }

@kotlinx.serialization.Serializable
data class FieldTransform(
    val fieldPath: String,
    val setToServerValue: ServerValue? = null,
    val increment: Value? = null,
    val maximum: Value? = null,
    val minimum: Value? = null,
    val appendMissingElements: ArrayValue? = null,
    val removeAllFromArray: ArrayValue? = null
)
