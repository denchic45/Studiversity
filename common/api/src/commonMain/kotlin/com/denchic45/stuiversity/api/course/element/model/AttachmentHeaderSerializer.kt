package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object AttachmentHeaderSerializer : JsonContentPolymorphicSerializer<AttachmentHeader>(AttachmentHeader::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AttachmentHeader> {
        return when (Json.decodeFromJsonElement<AttachmentType>(element.jsonObject.getValue("type"))) {
            AttachmentType.FILE -> FileAttachmentHeader.serializer()
            AttachmentType.LINK -> LinkAttachmentHeader.serializer()
        }
    }
}