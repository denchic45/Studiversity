package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object AttachmentSerializer : JsonContentPolymorphicSerializer<AttachmentResponse>(AttachmentResponse::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out AttachmentResponse> {
        return when {
            element.jsonObject.containsKey("bytes") -> FileAttachmentResponse.serializer()
            element.jsonObject.containsKey("url") -> LinkAttachmentResponse.serializer()
            else -> throw IllegalStateException()
        }
    }
}