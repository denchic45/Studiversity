package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object AttachmentSerializer : JsonContentPolymorphicSerializer<AttachmentHeader>(AttachmentHeader::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out AttachmentHeader> {
        return when (Json.decodeFromJsonElement<AttachmentType>(element.jsonObject.getValue("type"))) {
            AttachmentType.FILE -> FileAttachmentHeader.serializer()
            AttachmentType.LINK -> LinkAttachmentHeader.serializer()
        }
    }
}