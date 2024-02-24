package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object AttachmentSerializer : JsonContentPolymorphicSerializer<AttachmentResponse>(AttachmentResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AttachmentResponse> {
        return when {
            element.jsonObject.containsKey("bytes") -> FileAttachmentResponse.serializer()
            element.jsonObject.containsKey("url") -> LinkAttachmentResponse.serializer()
            else -> throw IllegalStateException()
        }
    }
}